package com.aduanas.comex.cargams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCargaRequestDTO {

    @NotBlank
    @Size(max = 50)
    private String nroDeclaracion;

    @NotBlank
    @Size(max = 255)
    private String descripcion;

    @NotBlank
    @Size(max = 100)
    private String paisOrigen;

    @NotBlank
    @Positive
    private String valorDeclarado;

    @NotBlank
    @Size(max = 12)
    private String importadorRut;

}
