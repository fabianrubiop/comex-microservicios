package com.aduanas.comex.notificacion_ms.exception;

public class NotificacionException
        extends RuntimeException {

    public NotificacionException(
            String mensaje
    ) {

        super(mensaje);
    }
}