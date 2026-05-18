package com.aduanas.comex.cargams.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CargaResponseDTO {
    private Long id;
    private String numeroDeclaracion;
    private String descripcion;
    private String paisOrigen;
    private BigDecimal valorDeclarado;
    private String importadorRut;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Datos cruzados agregados dinámicamente desde clasificacion-ms
    private Boolean permitido;
    private BigDecimal montoImpuesto;
    private String observaciones;
}