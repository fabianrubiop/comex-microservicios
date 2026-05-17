package com.aduanas.comex.riesgo_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RiesgoResponseDTO {

    private String nivelRiesgo;
    private boolean requiereInspeccion;
}
