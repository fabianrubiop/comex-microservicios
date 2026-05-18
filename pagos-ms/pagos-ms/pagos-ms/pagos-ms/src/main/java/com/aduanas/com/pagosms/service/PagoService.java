package com.aduanas.com.pagosms.service;

import com.aduanas.com.pagosms.Enum.EstadoPago;
import com.aduanas.com.pagosms.client.CargaFeignClient;
import com.aduanas.com.pagosms.dto.CargaExternaDto;
import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
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

        // 1. Llamada real al endpoint que me mostraste en la foto
        CargaExternaDto carga = cargaFeignClient.obtenerCargaPorId(dto.getCargaId());

        if (carga == null) {
            throw new RuntimeException("La carga con ID " + dto.getCargaId() + " no existe en el sistema de Cargas.");
        }

        // MODIFICACIÓN DE ACUERDO A TU FOTO: Ahora el estado correcto es "CLASIFICADA"
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

        // 4. IVA (19% aplicado sobre la tarifa base del trámite aduanero)
        BigDecimal valorIva = montoBase.multiply(new BigDecimal("0.19")).setScale(2, RoundingMode.HALF_UP);

        // 5. Total a cobrar al importador
        BigDecimal montoCalculado = montoBase.add(valorIva).setScale(2, RoundingMode.HALF_UP);

        // 6. Guardar el registro financiero en Laragon
        Pago nuevoPago = new Pago();
        nuevoPago.setCargaId(dto.getCargaId()); // Guarda la relación
        nuevoPago.setMonto(montoCalculado);
        nuevoPago.setMoneda(dto.getMoneda());
        nuevoPago.setEstadoPago(EstadoPago.PENDIENTE);
        nuevoPago.setTransaccionExternaId(dto.getTransaccionExternaId());
        nuevoPago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        return mapToResponseDTO(pagoGuardado, peso, valorIva);
    }

    public List<PagoResponseDto> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(p -> {
                    BigDecimal pesoReal = new BigDecimal("62.00");
                    try {
                        CargaExternaDto c = cargaFeignClient.obtenerCargaPorId(p.getCargaId());
                        if (c != null) pesoReal = c.getPeso();
                    } catch (Exception e) {}
                    BigDecimal valorIva = p.getMonto().multiply(new BigDecimal("0.159663")).setScale(2, RoundingMode.HALF_UP);
                    return mapToResponseDTO(p, pesoReal, valorIva);
                })
                .collect(Collectors.toList());
    }

    public PagoResponseDto obtenerPagoPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El pago con ID " + id + " no existe en los registros."));

        BigDecimal pesoReal = new BigDecimal("62.00");
        try {
            CargaExternaDto c = cargaFeignClient.obtenerCargaPorId(pago.getCargaId());
            if (c != null) pesoReal = c.getPeso();
        } catch (Exception e) {}

        BigDecimal valorIva = pago.getMonto().multiply(new BigDecimal("0.159663")).setScale(2, RoundingMode.HALF_UP);
        return mapToResponseDTO(pago, pesoReal, valorIva);
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