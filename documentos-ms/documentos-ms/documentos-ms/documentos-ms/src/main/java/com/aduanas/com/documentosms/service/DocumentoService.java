package com.aduanas.com.documentosms.service;

import com.aduanas.com.documentosms.client.ClasificacionFeignClient;
import com.aduanas.com.documentosms.client.NotificacionFeignClient;
import com.aduanas.com.documentosms.dto.*;
import com.aduanas.com.documentosms.entity.Documento;
import com.aduanas.com.documentosms.Enum.EstadoValidacion;
import com.aduanas.com.documentosms.Enum.EstadoValidacionArchivo;
import com.aduanas.com.documentosms.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ClasificacionFeignClient clasificacionFeignClient; // <-- CONECTADO
    private final NotificacionFeignClient notificacionFeignClient;   // <-- CONECTADO

    public DocumentoResponseUsuarioExDTO clienteSubeDocumento(DocumentoRequestUsuarioExDTO dto) {
        log.info("Gatillando Feign para verificar si existe la Carga ID: {}", dto.getCargaId());

        // 1. Validamos mediante Feign con Clasificación si la carga existe. Si no existe, esto tira error 404 altiro.
        clasificacionFeignClient.obtenerCargaPorId(dto.getCargaId());

        // 2. Si existía, creamos el documento amarrado a esa carga
        Documento documento = new Documento();
        documento.setCargaId(dto.getCargaId()); // Guardamos la relación
        documento.setTipoDocumento(dto.getTipoDocumento());
        documento.setRutaArchivo("/sistema/almacenamiento/aduana/" + dto.getTipoDocumento().toLowerCase() + "_simulado.pdf");
        documento.setObservacionManual("Comentario Cliente: " + dto.getObservacionManual());
        documento.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        documento.setFechaDocumento(LocalDateTime.now());

        return mapToUsuarioExDTO(documentoRepository.save(documento));
    }

    public Optional<DocumentoResponseAnalistaDTO> analistaRevisaDocumento(Long id, DocumentoRequestAnalistaDTO dto) {
        return documentoRepository.findById(id).map(existente -> {

            existente.setResultadoRevision(dto.getResultadoRevision());

            if (dto.getResultadoRevision() == EstadoValidacionArchivo.PDF_VALIDO) {
                existente.setEstadoValidacion(EstadoValidacion.VALIDADO);
                existente.setObservacionManual("Revisión Manual Exitosa: " + dto.getObservaciones());

            } else if (dto.getResultadoRevision() == EstadoValidacionArchivo.MALWARE_DETECTADO) {
                existente.setEstadoValidacion(EstadoValidacion.RECHAZADO);
                existente.setObservacionManual("[ALERTA DE SEGURIDAD] " + dto.getObservaciones());

            } else if (dto.getResultadoRevision() == EstadoValidacionArchivo.FORMATO_INCORRECTO) {
                existente.setEstadoValidacion(EstadoValidacion.RECHAZADO);
                existente.setObservacionManual("Rechazado por formato: " + dto.getObservaciones());
            }

            existente.setFechaDocumento(LocalDateTime.now());
            Documento guardado = documentoRepository.save(existente);

            // 🔥 ¡EL COLAZO AUTOMÁTICO HACIA NOTIFICACIÓN!
            try {
                log.info("Llamando a notificacion-ms para avisar el cambio de estado del documento {}", guardado.getDocumentoId());

                // CORREGIDO: Se adaptaron los argumentos para cumplir con los requerimientos del microservicio de notificaciones
                String emailSimulado = "analista.aduana@comex.cl";
                String mensajeAlerta = "Documento ID: " + guardado.getDocumentoId() +
                        " cambio a Estado: " + guardado.getEstadoValidacion().name() +
                        ". Obs: " + guardado.getObservacionManual();

                notificacionFeignClient.enviarNotificacionDocumento(emailSimulado, mensajeAlerta);
            } catch (Exception e) {
                log.error("No se pudo enviar la notificación pero el documento quedó guardado igual: {}", e.getMessage());
            }

            return mapToAnalistaDTO(guardado);
        });
    }

    public DocumentoResponseAnalistaDTO buscarPorId(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con el ID: " + id));
        return mapToAnalistaDTO(documento);
    }

    public List<DocumentoResponseAnalistaDTO> listar() {
        return documentoRepository.findAll().stream()
                .map(this::mapToAnalistaDTO)
                .collect(Collectors.toList());
    }

    private DocumentoResponseUsuarioExDTO mapToUsuarioExDTO(Documento m) {
        String estadoStr = m.getEstadoValidacion() != null ? m.getEstadoValidacion().name() : null;
        return new DocumentoResponseUsuarioExDTO(
                m.getDocumentoId(),
                m.getTipoDocumento(),
                estadoStr,
                m.getFechaDocumento()
        );
    }

    private DocumentoResponseAnalistaDTO mapToAnalistaDTO(Documento m) {
        String estadoStr = m.getEstadoValidacion() != null ? m.getEstadoValidacion().name() : null;
        return new DocumentoResponseAnalistaDTO(
                m.getDocumentoId(),
                estadoStr,
                m.getObservacionManual(),
                m.getFechaDocumento()
        );
    }
}