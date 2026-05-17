package com.aduanas.com.documentosms.service;

import com.aduanas.com.documentosms.dto.DocumentoRequestAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoRequestUsuarioExDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseUsuarioExDTO;
import com.aduanas.com.documentosms.entity.Documento;
import com.aduanas.com.documentosms.entity.EstadoValidacion;
import com.aduanas.com.documentosms.entity.EstadoValidacionArchivo;
import com.aduanas.com.documentosms.exception.GlobalExceptionHandler;
import com.aduanas.com.documentosms.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    // ==========================================
    // MÉTODO: CLIENTE SUBE DOCUMENTO (FLUJO 1)
    // ==========================================
    public DocumentoResponseUsuarioExDTO clienteSubeDocumento(DocumentoRequestUsuarioExDTO dto) {
        Documento documento = new Documento();
        documento.setTipoDocumento(dto.getTipoDocumento());
        documento.setRutaArchivo("/sistema/almacenamiento/aduana/" + dto.getTipoDocumento().toLowerCase() + "_simulado.pdf");
        documento.setObservacionManual("Comentario Cliente: " + dto.getObservacionManual());
        documento.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        documento.setFechaDocumento(LocalDateTime.now());

        return mapToUsuarioExDTO(documentoRepository.save(documento));
    }

    // ==========================================
    // MÉTODO: ANALISTA REVISA DOCUMENTO (FLUJO 2)
    // ==========================================
    public Optional<DocumentoResponseAnalistaDTO> analistaRevisaDocumento(Long id, DocumentoRequestAnalistaDTO dto) {
        return documentoRepository.findById(id).map(existente -> {

            // Evaluamos según tu Enum de simulación (EstadoValidacionArchivo)
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
            return mapToAnalistaDTO(documentoRepository.save(existente));
        });
    }

    // ==========================================
    // TRADUCTORES PRIVADOS (ESTILO MÉDICO CORREGIDO)
    // ==========================================

    // Calza con: id, tipo, estado, fechaCreacion
    private DocumentoResponseUsuarioExDTO mapToUsuarioExDTO(Documento m) {
        return new DocumentoResponseUsuarioExDTO(
                m.getDocumentoId(),
                m.getTipoDocumento(),
                m.getEstadoValidacion().name(),
                m.getFechaDocumento() // Pasa al constructor de fechaCreacion
        );
    }

    public DocumentoResponseAnalistaDTO buscarPorId(Long id) {
        // 1. Buscamos el documento en la BD usando tu repositorio
        Documento documento = documentoRepository.findById(id)
                // Si NO existe, lanzamos una RuntimeException para que la atrape tu GlobalExceptionHandler
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con el ID: " + id));

        // 2. Convertimos la Entidad a DTO usando el traductor privado que ya tienes abajo
        return mapToAnalistaDTO(documento);
    }

    // Calza con: id, estado, datosExtraidos, fechaRevision
    private DocumentoResponseAnalistaDTO mapToAnalistaDTO(Documento m) {
        return new DocumentoResponseAnalistaDTO(
                m.getDocumentoId(),
                m.getEstadoValidacion().name(),
                m.getObservacionManual(), // Pasa al constructor de datosExtraidos
                m.getFechaDocumento()     // Pasa al constructor de fechaRevision
        );
    }
}