package com.aduanas.comex.notificacion_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NotificacionResponseDTO {

    private Long id;

    private String mensaje;

    private String tipo;

    private String destinatario;

    private String estado;

    private LocalDateTime fecha;
}
