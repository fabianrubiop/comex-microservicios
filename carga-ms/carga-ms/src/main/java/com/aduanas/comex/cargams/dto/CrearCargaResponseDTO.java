package com.aduanas.comex.cargams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCargaResponseDTO {


    private Long cargaId;
    private String nroDeclaracion;
    private String descripcion;
    private String paisOrigen;
    private BigDecimal valorDeclarado;
    private String importadorRut;
    private String estado;
    private LocalDateTime fechaCreacion;

}
