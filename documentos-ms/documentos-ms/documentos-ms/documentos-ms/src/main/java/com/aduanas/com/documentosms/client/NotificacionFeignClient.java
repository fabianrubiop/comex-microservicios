package com.aduanas.com.documentosms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// CORREGIDO: Apunta al puerto definitivo 8087 y usa la ruta base /notificaciones sin prefijos falsos
@FeignClient(name = "notificacion-ms", url = "http://localhost:8087/notificaciones")
public interface NotificacionFeignClient {

    // CORREGIDO: Adaptado para emparejar exactamente con el endpoint "recibirAlerta" de notificacion-ms
    @PostMapping("/enviar-alerta")
    void enviarNotificacionDocumento(
            @RequestParam("email") String email,
            @RequestParam("mensaje") String mensaje
    );
}