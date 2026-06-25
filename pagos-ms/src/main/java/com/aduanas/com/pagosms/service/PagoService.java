package com.aduanas.com.pagosms.service;

import com.aduanas.com.pagosms.Enum.EstadoCarga;
import com.aduanas.com.pagosms.Enum.EstadoPago;
import com.aduanas.com.pagosms.client.ClasificacionFeignClient;
import com.aduanas.com.pagosms.dto.*;
import com.aduanas.com.pagosms.entity.Pago;
import com.aduanas.com.pagosms.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ClasificacionFeignClient clasificacionFeignClient;

    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDto procesarPago(PagoRequestDto dto) {
        CargaExternaDto carga = clasificacionFeignClient.obtenerCargaPorId(dto.getIdCarga());

        if (carga == null) {
            throw new RuntimeException("La carga con ID " + dto.getIdCarga() + " no existe.");
        }

        if (carga.getEstado() != EstadoCarga.PENDIENTE_PAGO) {
            throw new RuntimeException("Error de estado: La carga debe estar en PENDIENTE_PAGO.");
        }

        // Lógica de tramos y montos...
        BigDecimal peso = carga.getPeso();
        BigDecimal montoBase;
        String cargoFijo;
        String tramo;

        if (peso.compareTo(new BigDecimal("1000")) >= 0 && peso.compareTo(new BigDecimal("1999")) <= 0) {
            montoBase = new BigDecimal("10");
            cargoFijo = "$50.000 CLP";
            tramo = "Tramo 1";
        } else {
            montoBase = new BigDecimal("10");
            cargoFijo = "$0 CLP";
            tramo = "Carga General";
        }

        BigDecimal cargoFijoNumerico = new BigDecimal(cargoFijo.replaceAll("[^0-9]", ""));
        BigDecimal valorIva = (carga.getMontoImpuesto() != null) ? carga.getMontoImpuesto() : BigDecimal.ZERO;
        BigDecimal montoCalculado = montoBase.add(cargoFijoNumerico).add(valorIva).setScale(2, RoundingMode.HALF_UP);

        // 🛠️ MEJORA 1: Si no envías ID en el Paso 9, generamos uno temporal de SOLICITUD
        String idExterna = (dto.getIdTransaccionExterna() != null && !dto.getIdTransaccionExterna().isEmpty())
                ? dto.getIdTransaccionExterna()
                : "SOLICITUD-" + dto.getIdCarga();

        Pago nuevoPago = Pago.builder()
                .idCarga(dto.getIdCarga())
                .monto(montoCalculado)
                .moneda(dto.getMoneda())
                .estadoPago(EstadoPago.PENDIENTE)
                .idTransaccionExterna(idExterna) // Guardamos el ID de solicitud
                .fechaCreacion(LocalDateTime.now())
                .build();

        return mapToResponseDto(pagoRepository.save(nuevoPago), tramo, cargoFijo, valorIva, "Orden Generada - Esperando Banco");
    }

    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDto confirmarPagoDesdeBanco(NotificacionBancoDto bancoDto) {
        Pago pago = pagoRepository.findById(bancoDto.getIdPago())
                .orElseThrow(() -> new RuntimeException("Pago ID " + bancoDto.getIdPago() + " no existe."));

        String mensajeHumanizado = bancoDto.getTransaccionExitosa()
                ? "Transacción Exitosa y Pago Correcto"
                : "Transacción Fallida - Fondos Insuficientes";

        pago.setEstadoPago(EstadoPago.PROCESANDO);

        if (bancoDto.getTransaccionExitosa()) {
            pago.setEstadoPago(EstadoPago.COMPLETADO);
            pago.setFechaPago(LocalDateTime.now());

            // 🛠️ MEJORA 2: Guardamos el váucher del banco SIN borrar el ID de solicitud anterior
            // Esto requiere que agregues 'private String voucherBancario' en Pago.java y PagoResponseDto.java
            // pago.setVoucherBancario(bancoDto.getIdTransaccionExterna());

            try {
                // Enviamos el váucher del banco a la Carga Maestra
                clasificacionFeignClient.actualizarEstadoLiberacion(pago.getIdCarga(), "LIBERADA", bancoDto.getIdTransaccionExterna());
            } catch (Exception e) { log.error("Fallo comunicación"); }
        } else {
            pago.setEstadoPago(EstadoPago.FALLIDO);
        }

        return mapToResponseDto(pagoRepository.save(pago), "Carga Verificada", "$0 CLP", BigDecimal.ZERO, mensajeHumanizado);
    }

    public List<PagoResponseDto> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(p -> mapToResponseDto(p, "Consulta General", "$0 CLP", BigDecimal.ZERO, "Historial"))
                .toList();
    }

    public PagoResponseDto obtenerPagoPorId(Long id) {
        Pago pago = pagoRepository.findById(id).orElseThrow(() -> new RuntimeException("No existe"));
        return mapToResponseDto(pago, "Consulta Individual", "$0 CLP", BigDecimal.ZERO, "Detalle");
    }

    private PagoResponseDto mapToResponseDto(Pago p, String tramo, String cargoFijo, BigDecimal valorIva, String msj) {
        return PagoResponseDto.builder()
                .idPago(p.getIdPago())
                .idCarga(p.getIdCarga())
                .tramo(tramo)
                .cargoFijo(cargoFijo)
                .montoImpuesto(valorIva)
                .montoTotal(p.getMonto())
                .moneda(p.getMoneda())
                .estadoPago(p.getEstadoPago() != null ? p.getEstadoPago().name() : null)
                .idTransaccionExterna(p.getIdTransaccionExterna())
                .fechaCreacion(p.getFechaCreacion())
                .fechaPago(p.getFechaPago())
                .resultadoTransaccion(msj)
                .build();
    }
}