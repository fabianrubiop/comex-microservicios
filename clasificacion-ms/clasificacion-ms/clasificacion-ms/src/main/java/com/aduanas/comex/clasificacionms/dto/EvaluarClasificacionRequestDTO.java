package com.aduanas.comex.clasificacionms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaluarClasificacionRequestDTO {

    @NotNull
    private Long cargaId;

    @NotBlank
    private String descripcionMercancia;

    @NotBlank
    private String paisOrigen;

    @NotNull
    @Positive
    private BigDecimal valorDeclarado;
}