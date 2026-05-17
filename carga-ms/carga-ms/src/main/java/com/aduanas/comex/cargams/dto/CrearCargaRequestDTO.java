package com.aduanas.comex.cargams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCargaRequestDTO {

    @NotBlank(message = "EL NUMERO DE DECLARACION NO PUEDE ESTAR VACIO")
    @Size(max = 50)
    private String nroDeclaracion;

    @NotBlank(message = "DEBE INCLUIR UNA DESCRIPCION")
    @Size(max = 255)
    private String descripcion;

    @NotNull(message = "EL PAIS DE ORIGEN ES OBLIGATORIO")
    @Size(max = 100)
    private String paisOrigen;

    @NotNull(message = "DEBE INGRESAR VALOR DECLARADO DE CARGA")
    @Positive
    private BigDecimal valorDeclarado;

    @NotNull(message = "RUT DE IMPORTADOR ES OBLIGATORIO")
    @Size(max = 12)
    private String importadorRut;

}
