package com.aduanas.com.pagosms.service;

import com.aduanas.com.pagosms.Enum.EstadoPago;
import com.aduanas.com.pagosms.client.CargaFeignClient;
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
    private final CargaFeignClient cargaFeignClient;

    public PagoResponseDto procesarPago(PagoRequestDto dto) {
        // 1. Llamada real al endpoint de Cargas vía Feign
        CargaExternaDto carga = cargaFeignClient.obtenerCargaPorId(dto.getCargaId());

        if (carga == null) {
            throw new RuntimeException("La carga con ID " + dto.getCargaId() + " no existe en el sistema de Cargas.");
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

        // 2. Tramos de tarifa plana por peso puro
        if (peso.compareTo(mil) >= 0 && peso.compareTo(new BigDecimal("1999")) <= 0) {
            montoBase = new BigDecimal("200000"); // Tramo 1
        } else if (peso.compareTo(dosMil) >= 0 && peso.compareTo(new BigDecimal("2999")) <= 0) {
            montoBase = new BigDecimal("160000"); // Tramo 2
        } else if (peso.compareTo(tresMil) >= 0 && peso.compareTo(new BigDecimal("3999")) <= 0) {
            montoBase = new BigDecimal("110000"); // Tramo 3
        } else if (peso.compareTo(cuatroMil) >= 0 && peso.compareTo(new BigDecimal("4999")) <= 0) {
            montoBase = new BigDecimal("90000");  // Tramo 4
        } else if (peso.compareTo(cincoMil) >= 0) {
            montoBase = new BigDecimal("70000");  // Tramo 5
        } else {
            montoBase = new BigDecimal("50000");  // Menos de 1000kg (Como tus 62kg de la foto)
        }

        // 3. Regla del Administrativo (Sólo si cae en Tramo 1)
        if (peso.compareTo(mil) >= 0 && peso.compareTo(new BigDecimal("1999")) <= 0) {
            montoBase = montoBase.add(new BigDecimal("50000"));
        }

        // 4. MODIFICACIÓN ADUANAS: Rescatamos el IVA/Impuesto directo de la Clasificación
        BigDecimal valorIva = carga.getMontoImpuesto();
        if (valorIva == null) {
            valorIva = BigDecimal.ZERO; // Respaldo por si viniera nulo
        }

        // 5. Total a cobrar al importador (Base Tramo + Impuesto de Clasificación)
        BigDecimal montoCalculado = montoBase.add(valorIva).setScale(2, RoundingMode.HALF_UP);

        // 6. Guardar el registro financiero inicial en Laragon
        Pago nuevoPago = new Pago();
        nuevoPago.setCargaId(dto.getCargaId());
        nuevoPago.setMonto(montoCalculado);
        nuevoPago.setMoneda(dto.getMoneda());
        nuevoPago.setEstadoPago(EstadoPago.PENDIENTE); // Nace esperando la respuesta del banco
        nuevoPago.setTransaccionExternaId(dto.getTransaccionExternaId());
        nuevoPago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        return mapToResponseDTO(pagoGuardado, peso, valorIva);
    }

    // =========================================================================
    // 🏦 SIMULADOR BANCARIO: Confirma el pago, cambia a COMPLETADO y libera la Carga
    // =========================================================================
    public PagoResponseDto confirmarPagoDesdeBanco(NotificacionBancoDto bancoDto) {
        // 1. Buscar el pago previo en Laragon
        Pago pago = pagoRepository.findById(bancoDto.getPagoId())
                .orElseThrow(() -> new RuntimeException("El pago con ID " + bancoDto.getPagoId() + " no existe."));

        BigDecimal pesoReal = new BigDecimal("62.00");
        BigDecimal impuestoReal = BigDecimal.ZERO;

        try {
            CargaExternaDto c = cargaFeignClient.obtenerCargaPorId(bancoDto.getCargaId());
            if (c != null) {
                pesoReal = c.getPeso();
                if (c.getMontoImpuesto() != null) {
                    impuestoReal = c.getMontoImpuesto();
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener la carga externa mediante Feign.");
        }

        if (bancoDto.getTransaccionExitosa()) {
            // 2. Modificar el estado local a COMPLETADO con los datos del váucher manual
            pago.setEstadoPago(EstadoPago.COMPLETADO);
            pago.setTransaccionExternaId(bancoDto.getTransaccionExternalId());
            pago.setFechaPago(LocalDateTime.now());

            pagoRepository.save(pago);

            // 3. LA SINCRONIZACIÓN: Llamada PUT reactiva hacia el microservicio de Cargas
            try {
                cargaFeignClient.actualizarEstadoCarga(bancoDto.getCargaId(), "LIBERADA");
            } catch (Exception e) {
                throw new RuntimeException("Pago completado, pero falló la comunicación remota con Cargas: " + e.getMessage());
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
                        CargaExternaDto c = cargaFeignClient.obtenerCargaPorId(p.getCargaId());
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
            CargaExternaDto c = cargaFeignClient.obtenerCargaPorId(pago.getCargaId());
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