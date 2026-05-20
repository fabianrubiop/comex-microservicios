package com.aduanas.com.documentosms.dto;

import com.aduanas.com.documentosms.Enum.EstadoValidacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DocumentoResponseAnalistaDTO {

    private Long documentoId;
    private String estadoValidacion;
    private String datosExtraidos;     // Tu nombre real de variable
    private LocalDateTime fechaRevision; // Tu nombre real de variable


}
