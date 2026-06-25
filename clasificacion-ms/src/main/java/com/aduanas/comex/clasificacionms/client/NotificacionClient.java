package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notificacion-ms")
public interface NotificacionClient {

    // ✅ CORREGIDO: Ahora apunta a la ruta que SÍ existe: /enviar-alerta
    // ✅ CORREGIDO: Recibe un Map para enviarlo como JSON (@RequestBody)
    @PostMapping("/api/v1/notificaciones/enviar-alerta")
    void enviarAlertaJson(@RequestBody Map<String, Object> body);
}