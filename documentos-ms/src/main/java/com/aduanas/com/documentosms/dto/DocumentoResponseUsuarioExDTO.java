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
public class DocumentoResponseUsuarioExDTO extends RepresentationModel<DocumentoResponseUsuarioExDTO> {
    private Long idDocumento;
    private String tipoDocumento;
    private String estadoValidacion;
    private LocalDateTime fechaCreacion;
}