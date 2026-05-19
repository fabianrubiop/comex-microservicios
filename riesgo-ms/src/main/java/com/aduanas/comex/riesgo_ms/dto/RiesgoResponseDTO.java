package com.aduanas.comex.riesgo_ms.dto;

// LOMBOK
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// FECHA
import java.time.LocalDateTime;

// DTO salida
@Data
@NoArgsConstructor
@AllArgsConstructor

// Builder ayuda a crear objetos
@Builder
public class RiesgoResponseDTO {

    private Long id;
    private String descripcion;
    private String nivel;
    private String estado;
    private String tipoCarga;
    private String origen;
    private LocalDateTime fechaRegistro;
}