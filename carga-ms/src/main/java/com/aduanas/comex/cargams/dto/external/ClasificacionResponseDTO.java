package com.aduanas.comex.cargams.dto.external;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
public class ClasificacionResponseDTO extends RepresentationModel<ClasificacionResponseDTO> {
    private Long idClasificacion; // ✅ CORREGIDO: Especificado
    private Long idCarga;
    private String tipoClasificacion;
    private Boolean permitido;
    private BigDecimal montoImpuesto;
    private String observaciones;
}