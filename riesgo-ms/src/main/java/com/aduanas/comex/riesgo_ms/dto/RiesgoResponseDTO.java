package com.aduanas.comex.riesgo_ms.dto;



import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiesgoResponseDTO {

    private Long id;

    private String descripcion;

    private String tipoCarga;

    private String origen;

    private LocalDateTime fechaRegistro;

    private Long cargaId;

    private Integer puntajeRiesgo;

    private String canalAsignado;

    private String motivoAlerta;

    private LocalDateTime fechaEvaluacion;
}