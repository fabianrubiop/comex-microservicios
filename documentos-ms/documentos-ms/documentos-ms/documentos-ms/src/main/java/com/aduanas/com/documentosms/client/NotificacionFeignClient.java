package com.aduanas.com.documentosms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// "notificacion-ms" es el nombre con el que se registra el microservicio destino
@FeignClient(name = "notificacion-ms", url = "http://localhost:8085") // Ajusta el puerto real del servicio de correos
public interface NotificacionFeignClient {

    // Cambia esto según el endpoint POST real que tus compañeros expongan para mandar correos
    @PostMapping("/api/notificaciones/enviar-alerta")
    void enviarNotificacionDocumento(
            @RequestParam("documentoId") Long documentoId,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam("observaciones") String observaciones
    );
}