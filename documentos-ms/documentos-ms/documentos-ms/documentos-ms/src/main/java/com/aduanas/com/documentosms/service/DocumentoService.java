package com.aduanas.com.documentosms.service;

import com.aduanas.com.documentosms.dto.DocumentoRequestAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoRequestUsuarioExDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseUsuarioExDTO;
import com.aduanas.com.documentosms.entity.Documento;
import com.aduanas.com.documentosms.Enum.EstadoValidacion;
import com.aduanas.com.documentosms.Enum.EstadoValidacionArchivo;
import com.aduanas.com.documentosms.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    public DocumentoResponseUsuarioExDTO clienteSubeDocumento(DocumentoRequestUsuarioExDTO dto) {
        Documento documento = new Documento();
        documento.setTipoDocumento(dto.getTipoDocumento());
        documento.setRutaArchivo("/sistema/almacenamiento/aduana/" + dto.getTipoDocumento().toLowerCase() + "_simulado.pdf");
        documento.setObservacionManual("Comentario Cliente: " + dto.getObservacionManual());
        documento.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        documento.setFechaDocumento(LocalDateTime.now());

        return mapToUsuarioExDTO(documentoRepository.save(documento));
    }


    public Optional<DocumentoResponseAnalistaDTO> analistaRevisaDocumento(Long id, DocumentoRequestAnalistaDTO dto) {
        return documentoRepository.findById(id).map(existente -> {

            // Evaluamos el contenido usando el Enum correcto del DTO
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

    public DocumentoResponseAnalistaDTO buscarPorId(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con el ID: " + id));

        return mapToAnalistaDTO(documento);
    }


    private DocumentoResponseUsuarioExDTO mapToUsuarioExDTO(Documento m) {
        String estadoStr = null;
        if (m.getEstadoValidacion() != null) {
            // Forzamos el casteo explícito a tu Enum por si IntelliJ sigue mareado con carpetas viejas
            estadoStr = ((EstadoValidacion) m.getEstadoValidacion()).name();
        }

        return new DocumentoResponseUsuarioExDTO(
                m.getDocumentoId(),
                m.getTipoDocumento(),
                estadoStr,
                m.getFechaDocumento()
        );
    }

    private DocumentoResponseAnalistaDTO mapToAnalistaDTO(Documento m) {
        String estadoStr = null;
        if (m.getEstadoValidacion() != null) {
            estadoStr = ((EstadoValidacion) m.getEstadoValidacion()).name();
        }

        return new DocumentoResponseAnalistaDTO(
                m.getDocumentoId(),
                estadoStr,
                m.getObservacionManual(),
                m.getFechaDocumento()
        );
    }
}