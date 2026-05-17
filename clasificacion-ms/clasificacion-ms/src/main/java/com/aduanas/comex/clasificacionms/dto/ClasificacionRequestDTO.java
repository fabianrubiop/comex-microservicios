package com.aduanas.comex.clasificacionms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClasificacionRequestDTO {

    @NotNull
    private Long cargaId;

    @NotBlank
    private String descripcionMercancia;

    @NotBlank
    private String paisOrigen;

    @NotNull
    private BigDecimal valorDeclarado;
}
