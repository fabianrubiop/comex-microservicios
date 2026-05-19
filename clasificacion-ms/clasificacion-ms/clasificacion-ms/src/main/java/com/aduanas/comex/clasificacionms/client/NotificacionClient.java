package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notificacion-ms", url = "http://localhost:8086/api/v1/notificaciones")
public interface NotificacionClient {
    @PostMapping("/enviar-alerta")
    void enviarNotificacionClasificacion(
            @RequestParam("destino") String email,
            @RequestParam("mensaje") String mensaje
    );
}
