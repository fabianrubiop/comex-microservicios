package com.aduanas.comex.clasificacionms.service;

import com.aduanas.comex.clasificacionms.enums.EstadoCarga;
import com.aduanas.comex.clasificacionms.client.*;
import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import com.aduanas.comex.clasificacionms.enums.TipoClasificacion;
import com.aduanas.comex.clasificacionms.repository.ClasificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClasificacionService {

    private final ClasificacionRepository clasificacionRepository;
    private final CargaClient cargaClient;
    private final DocumentoClient documentoClient;
    private final PagoClient pagoClient;
    private final NotificacionClient notificacionClient;
    private final RiesgoClient riesgoClient; // <-- 1. CORREGIDO: Inyección del cliente de riesgos

    @Transactional(rollbackFor = Exception.class)
    public ClasificacionResponseDTO evaluar(EvaluarClasificacionRequestDTO dto) {
        log.info("Iniciando auditoría tributaria y análisis de riesgo para Carga ID: {}", dto.getCargaId());

        // =========================================================================
        // ANALISIS DE RIESGO ADUANERO 🕵️‍♂️
        // =========================================================================
        // Usamos un RUT genérico por defecto para la evaluación si no viene en el DTO extendido,
        // o puedes adaptar el DTO si necesitas pasar el RUT real desde carga-ms.
        String rutEvaluar = "12345678-9";
        boolean tieneRiesgo = false;

        try {
            log.info("Consultando matriz de riesgos en riesgo-ms para Origen: {}", dto.getPaisOrigen());
            tieneRiesgo = riesgoClient.evaluarRiesgoCarga(rutEvaluar, dto.getPaisOrigen());
        } catch (Exception e) {
            log.error("No se pudo conectar con riesgo-ms, se asume carga sin riesgo por contingencia: {}", e.getMessage());
        }

        // Si el microservicio de riesgo detecta una amenaza, bloqueamos la importación inmediatamente
        if (tieneRiesgo) {
            log.warn("ALERTA: Carga ID {} RECHAZADA por la matriz de riesgo aduanero.", dto.getCargaId());

            String obsRechazo = "Carga rechazada por el Departamento de Gestión de Riesgos. País de origen o importador bajo observación.";

            Clasificacion clasificacionRechazada = Clasificacion.builder()
                    .cargaId(dto.getCargaId())
                    .tipoClasificacion(TipoClasificacion.PROHIBIDA) // Enum contextual
                    .permitido(false)
                    .montoImpuesto(BigDecimal.ZERO)
                    .observaciones(obsRechazo)
                    .fechaEvaluacion(LocalDateTime.now())
                    .build();

            clasificacionRechazada = clasificacionRepository.save(clasificacionRechazada);

            // Notificamos de vuelta a carga-ms para cambiar el estado a RECHAZADA
            try {
                cargaClient.actualizarImpuestoYEstado(dto.getCargaId(), BigDecimal.ZERO, EstadoCarga.RECHAZADA);
            } catch (Exception e) {
                log.error("Error al actualizar estado de rechazo en carga-ms: {}", e.getMessage());
            }

            return mapToResponse(clasificacionRechazada);
        }

        // =========================================================================
        // CÁLCULO DE ADUANA CHILE COMPLETO (Si no hay riesgo) 🇨🇱
        // =========================================================================
        BigDecimal valorCarga = dto.getValorDeclarado();

        BigDecimal arancel = valorCarga.multiply(new BigDecimal("0.06"));
        BigDecimal baseIva = valorCarga.add(arancel);
        BigDecimal iva = baseIva.multiply(new BigDecimal("0.19"));
        BigDecimal impuestoTotal = arancel.add(iva);

        String observaciones = String.format(
                "Aprobado. Desglose: Arancel (6%%): $%s | IVA (19%%): $%s. Carga lista para recaudación.",
                arancel.setScale(2, RoundingMode.HALF_UP),
                iva.setScale(2, RoundingMode.HALF_UP)
        );

        Clasificacion clasificacion = Clasificacion.builder()
                .cargaId(dto.getCargaId())
                .tipoClasificacion(TipoClasificacion.IMPORTACION)
                .permitido(true)
                .montoImpuesto(impuestoTotal)
                .observaciones(observaciones)
                .fechaEvaluacion(LocalDateTime.now())
                .build();

        clasificacion = clasificacionRepository.save(clasificacion);
        log.info("Clasificación guardada de forma local con ID: {}", clasificacion.getId());

        // =========================================================================
        // ORQUESTACIÓN SÍNCRONA EN CASCADA
        // =========================================================================
        try {
            log.info("Notificando cambio de estado a carga-ms como PENDIENTE_PAGO");
            cargaClient.actualizarImpuestoYEstado(dto.getCargaId(), impuestoTotal, EstadoCarga.PENDIENTE_PAGO);

            documentoClient.generarDeclaracionIngreso(dto.getCargaId(), impuestoTotal, "12.345.678-9");

            pagoClient.crearOrdenDePago(dto.getCargaId(), impuestoTotal);

            notificacionClient.enviarNotificacionClasificacion(
                    "importaciones@comex.cl",
                    "Tu Carga N° " + dto.getCargaId() + " fue clasificada con éxito. Total aduana a pagar: $" + impuestoTotal
            );

        } catch (Exception e) {
            log.error("Alerta: Flujo en cadena interrumpido por un servicio caído: {}", e.getMessage());
        }

        return mapToResponse(clasificacion);
    }

    public List<ClasificacionResponseDTO> listar() {
        return clasificacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ClasificacionResponseDTO obtenerPorId(Long id) {
        Clasificacion clasificacion = clasificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clasificación no encontrada"));
        return mapToResponse(clasificacion);
    }

    public ClasificacionResponseDTO obtenerPorCargaId(Long cargaId) {
        Clasificacion clasificacion = clasificacionRepository.findByCargaId(cargaId)
                .orElseThrow(() -> new RuntimeException("Clasificación no encontrada para la carga: " + cargaId));
        return mapToResponse(clasificacion);
    }

    private ClasificacionResponseDTO mapToResponse(Clasificacion clasificacion) {
        return ClasificacionResponseDTO.builder()
                .id(clasificacion.getId())
                .cargaId(clasificacion.getCargaId())
                .tipoClasificacion(clasificacion.getTipoClasificacion().name())
                .permitido(clasificacion.getPermitido())
                .montoImpuesto(clasificacion.getMontoImpuesto())
                .observaciones(clasificacion.getObservaciones())
                .build();
    }
}