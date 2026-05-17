package com.aduanas.com.documentosms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseUsuarioExDTO {


        private Long documentoId;         // El ID auto-incrementable de la BD
        private String tipoDocumento;     // FACTURA o BL
        private String estadoValidacion;  // Va a retornar siempre "PENDIENTE" al principio
        private LocalDateTime fechaCreacion; // La fecha y hora exacta de la subida

}
