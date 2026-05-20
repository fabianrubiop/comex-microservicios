package com.aduanas.comex.riesgo_ms.dto;


import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiesgoRequestDTO {

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "El tipo de carga es obligatorio")
    private String tipoCarga;


    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotNull(message = "El ID de carga es obligatorio")
    private Long cargaId;

    @NotNull(message = "El puntaje de riesgo es obligatorio")
    @Min(value = 0, message = "El puntaje mínimo es 0")
    @Max(value = 100, message = "El puntaje máximo es 100")
    private Integer puntajeRiesgo;

    @NotBlank(message = "El canal asignado es obligatorio")
    private String canalAsignado;

    @NotBlank(message = "El motivo de alerta es obligatorio")
    private String motivoAlerta;
}