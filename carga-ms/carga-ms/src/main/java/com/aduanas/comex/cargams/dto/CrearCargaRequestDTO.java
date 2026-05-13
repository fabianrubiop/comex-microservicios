package com.aduanas.comex.cargams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCargaRequestDTO {

    private String nroDeclaracion;

    private String descripcion;

    private String paisOrigen;

    private String valorDeclarado;

    private String importadorRut;

}
