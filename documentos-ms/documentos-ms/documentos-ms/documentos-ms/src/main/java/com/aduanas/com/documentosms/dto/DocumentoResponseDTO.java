package com.aduanas.com.documentosms.dto;

import com.aduanas.com.documentosms.entity.EstadoValidacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DocumentoResponseDTO {

        private Long documentoId;

        private String rutaArchivo;

        private String tipoDocumento;

        private String datosExtraidos;

        private EstadoValidacion estadoValidacion;

        private LocalDateTime fechaDocumento;



}
