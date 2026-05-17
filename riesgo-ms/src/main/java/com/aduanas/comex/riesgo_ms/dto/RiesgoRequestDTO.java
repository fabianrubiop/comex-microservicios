package com.aduanas.comex.riesgo_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RiesgoRequestDTO {

    private Double valorDeclarado;
    private String paisOrigen;
}
