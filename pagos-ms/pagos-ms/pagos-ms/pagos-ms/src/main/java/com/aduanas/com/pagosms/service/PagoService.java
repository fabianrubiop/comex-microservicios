package com.aduanas.com.pagosms.service;

import com.aduanas.com.pagosms.Enum.EstadoPago;
import com.aduanas.com.pagosms.client.ClasificacionFeignClient; // 1. Cambiado al nuevo cliente oficial
import com.aduanas.com.pagosms.dto.CargaExternaDto;
import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
import com.aduanas.com.pagosms.dto.NotificacionBancoDto;
import com.aduanas.com.pagosms.entity.Pago;
import com.aduanas.com.pagosms.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ClasificacionFeignClient clasificacionFeignClient; // 2. Inyectamos el cliente correcto

    public PagoResponseDto procesarPago(PagoRequestDto dto) {
        // Llamada al endpoint de Clasificación para obtener la Carga
        CargaExternaDto carga = clasificacionFeignClient.obtenerCargaPorId(dto.getCargaId());

        if (carga == null) {
            throw new RuntimeException("La carga con ID " + dto.getCargaId() + " no existe en el sistema.");
        }

        // Validación de Estado de la Carga
        if (!"CLASIFICADA".equalsIgnoreCase(carga.getEstado())) {
            throw new RuntimeException("No se puede procesar el pago. La carga requiere estar CLASIFICADA. Estado actual: " + carga.getEstado());
        }

        BigDecimal peso = carga.getPeso();
        BigDecimal montoBase;

        BigDecimal mil = new BigDecimal("1000");
        BigDecimal dosMil = new BigDecimal("2000");
        BigDecimal tresMil = new BigDecimal("3000");
        BigDecimal cuatroMil = new BigDecimal("4000");
        BigDecimal cincoMil = new BigDecimal("5000");

        // Tramos de tarifa plana por peso puro
        if (peso.compareTo(mil) >= 0 && peso.compareTo(new BigDecimal("1999")) <= 0) {
            montoBase = new BigDecimal("200000");
        } else if (peso.compareTo(dosMil) >= 0 && peso.compareTo(new BigDecimal("2999")) <= 0) {
            montoBase = new BigDecimal("160000");
        } else if (peso.compareTo(tresMil) >= 0 && peso.compareTo(new BigDecimal("3999")) <= 0) { // Corregido rango lógico
            montoBase = new BigDecimal("110000");
        } else if (peso.compareTo(cuatroMil) >= 0 && peso.compareTo(new BigDecimal("4999")) <= 0) {
            montoBase = new BigDecimal("90000");
        } else if (peso.compareTo(cincoMil) >= 0) {
            montoBase = new BigDecimal("70000");
        } else {
            montoBase = new BigDecimal("50000");
        }

        // Regla del Administrativo (Sólo si cae en Tramo 1)
        if (peso.compareTo(mil) >= 0 && peso.compareTo(new BigDecimal("1999")) <= 0) {
            montoBase = montoBase.add(new BigDecimal("50000"));
        }

        // Rescatamos el IVA/Impuesto directo de la Clasificación
        BigDecimal valorIva = carga.getMontoImpuesto();
        if (valorIva == null) {
            valorIva = BigDecimal.ZERO;
        }

        // Total a cobrar (Base Tramo + Impuesto de Clasificación)
        BigDecimal montoCalculado = montoBase.add(valorIva).setScale(2, RoundingMode.HALF_UP);

        // Guardar el registro financiero inicial en Laragon
        Pago nuevoPago = new Pago();
        nuevoPago.setCargaId(dto.getCargaId());
        nuevoPago.setMonto(montoCalculado);
        nuevoPago.setMoneda(dto.getMoneda());
        nuevoPago.setEstadoPago(EstadoPago.PENDIENTE);
        nuevoPago.setTransaccionExternaId(dto.getTransaccionExternaId());
        nuevoPago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        return mapToResponseDTO(pagoGuardado, peso, valorIva);
    }

    // =========================================================================
    // 🏦 SIMULADOR BANCARIO CON LÓGICA DE CONTROL "PROCESANDO"
    // =========================================================================
    public PagoResponseDto confirmarPagoDesdeBanco(NotificacionBancoDto bancoDto) {
        Pago pago = pagoRepository.findById(bancoDto.getPagoId())
                .orElseThrow(() -> new RuntimeException("El pago con ID " + bancoDto.getPagoId() + " no existe."));

        // CONGELAR EN "PROCESANDO"
        pago.setEstadoPago(EstadoPago.PROCESANDO);
        pagoRepository.save(pago);

        BigDecimal pesoReal = new BigDecimal("62.00");
        BigDecimal impuestoReal = BigDecimal.ZERO;

        try {
            CargaExternaDto c = clasificacionFeignClient.obtenerCargaPorId(bancoDto.getCargaId());
            if (c != null) {
                pesoReal = c.getPeso();
                if (c.getMontoImpuesto() != null) {
                    impuestoReal = c.getMontoImpuesto();
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener la carga mediante ClasificacionFeignClient.");
        }

        // Evaluamos la respuesta definitiva del banco
        if (bancoDto.getTransaccionExitosa()) {
            pago.setEstadoPago(EstadoPago.COMPLETADO);
            pago.setTransaccionExternaId(bancoDto.getTransaccionExternalId());
            pago.setFechaPago(LocalDateTime.now());

            pagoRepository.save(pago);

            // 🔄 LA SINCRONIZACIÓN CORREGIDA HACIA CLASIFICACIÓN
            try {
                // Mandamos "APROBADO" para liberar la carga como lo exige tu mapa
                clasificacionFeignClient.actualizarEstadoLiberacion(bancoDto.getCargaId(), "APROBADO");
            } catch (Exception e) {
                throw new RuntimeException("Pago completado, pero falló la comunicación remota con Clasificación: " + e.getMessage());
            }
        } else {
            pago.setEstadoPago(EstadoPago.FALLIDO);
            pagoRepository.save(pago);
        }

        return mapToResponseDTO(pago, pesoReal, impuestoReal);
    }

    public List<PagoResponseDto> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(p -> {
                    BigDecimal pesoReal = new BigDecimal("62.00");
                    BigDecimal impuestoReal = BigDecimal.ZERO;
                    try {
                        CargaExternaDto c = clasificacionFeignClient.obtenerCargaPorId(p.getCargaId());
                        if (c != null) {
                            pesoReal = c.getPeso();
                            if (c.getMontoImpuesto() != null) impuestoReal = c.getMontoImpuesto();
                        }
                    } catch (Exception e) {}
                    return mapToResponseDTO(p, pesoReal, impuestoReal);
                })
                .collect(Collectors.toList());
    }

    public PagoResponseDto obtenerPagoPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El pago con ID " + id + " no existe en los registros."));

        BigDecimal pesoReal = new BigDecimal("62.00");
        BigDecimal impuestoReal = BigDecimal.ZERO;
        try {
            CargaExternaDto c = clasificacionFeignClient.obtenerCargaPorId(pago.getCargaId());
            if (c != null) {
                pesoReal = c.getPeso();
                if (c.getMontoImpuesto() != null) impuestoReal = c.getMontoImpuesto();
            }
        } catch (Exception e) {}

        return mapToResponseDTO(pago, pesoReal, impuestoReal);
    }

    private PagoResponseDto mapToResponseDTO(Pago p, BigDecimal peso, BigDecimal valorIva) {
        String tramo;
        String cargoFijo = "$0 CLP";

        BigDecimal mil = new BigDecimal("1000");
        BigDecimal milNovecientos = new BigDecimal("1999");

        if (peso.compareTo(mil) >= 0 && peso.compareTo(milNovecientos) <= 0) {
            tramo = "Tramo 1: 1.000kg a 1.999kg (Base: 200.000 CLP)";
            cargoFijo = "$50.000 CLP";
        } else {
            tramo = "Carga General (" + peso + " kg) - Tarifa Plana correspondiente";
        }

        return new PagoResponseDto(
                p.getPagoId(),
                p.getCargaId(),
                tramo,
                cargoFijo,
                valorIva,
                p.getMonto(),
                p.getMoneda(),
                p.getEstadoPago(),
                p.getTransaccionExternaId(),
                p.getFechaPago()
        );
    }
}