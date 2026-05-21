package com.aduanas.comex.notificacion_ms.dto;

// VALIDACIONES
import jakarta.validation.constraints.NotBlank;

// LOMBOK
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDTO {

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}