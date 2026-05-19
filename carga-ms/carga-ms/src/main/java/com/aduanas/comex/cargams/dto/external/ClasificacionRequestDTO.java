package com.aduanas.comex.cargams.dto.external;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ClasificacionRequestDTO {
    private Long cargaId;
    private String descripcionMercancia;
    private String paisOrigen;
    private BigDecimal valorDeclarado;
}
