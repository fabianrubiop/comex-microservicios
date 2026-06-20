package com.aduanas.com.documentosms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseAnalistaDTO extends RepresentationModel<DocumentoResponseAnalistaDTO> {
    private Long idDocumento;
    private String estadoValidacion;
    private String datosExtraidos;
    private LocalDateTime fechaRevision;
}