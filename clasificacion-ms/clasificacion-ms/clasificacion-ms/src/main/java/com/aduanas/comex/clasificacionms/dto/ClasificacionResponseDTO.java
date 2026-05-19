package com.aduanas.comex.clasificacionms.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClasificacionResponseDTO {

    private Long id;
    private Long cargaId;
    private String tipoClasificacion;
    private Boolean permitido;
    private BigDecimal montoImpuesto;
    private String observaciones;

}