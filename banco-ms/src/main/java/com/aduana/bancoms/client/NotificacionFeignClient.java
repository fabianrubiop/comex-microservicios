package com.aduana.bancoms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Apunta al puerto 8087 donde vive el microservicio de notificaciones
@FeignClient(name = "notificacion-ms", url = "http://localhost:8087")
public interface NotificacionFeignClient {

    // Llama al endpoint de enviar-alerta que configuramos en notificacion-ms
    @PostMapping("/api/v1/notificaciones/enviar-alerta")
    void enviar(@RequestBody Object notificacionBody);
}