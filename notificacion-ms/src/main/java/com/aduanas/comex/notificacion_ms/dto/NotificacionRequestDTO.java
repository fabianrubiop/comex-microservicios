package com.aduanas.comex.notificacion_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class NotificacionRequestDTO {

    private String destinatario;
    private String mensaje;
    private String tipo;
    private String estado;


}
