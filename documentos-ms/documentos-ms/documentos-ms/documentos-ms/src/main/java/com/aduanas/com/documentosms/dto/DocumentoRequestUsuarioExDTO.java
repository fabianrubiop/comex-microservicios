package com.aduanas.com.documentosms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class DocumentoRequestUsuarioExDTO {


    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message  = "La observaciones no pueden estar vacias")
    private String observacionManual;

    // Agrega esto adentro de tu DocumentoRequestUsuarioExDTO
    @NotNull(message = "El ID de la carga es obligatorio")
    private Long cargaId;



}