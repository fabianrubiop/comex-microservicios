package com.aduana.bancoms.controller;

import com.aduana.bancoms.client.NotificacionFeignClient;
import com.aduana.bancoms.dto.TransaccionRequestDto;
import com.aduana.bancoms.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService bancoService;

    private final NotificacionFeignClient notificacionClient;

    // ✅ CORREGIDO: URL real mapeada según los RequestMapping del proyecto
    // POST: http://localhost:8086/api/v1/banco/procesar
    @PostMapping("/procesar")
    public ResponseEntity<Map<String, String>> pagarCargaAduanera(@RequestBody TransaccionRequestDto dto) {

        // Llama a la lógica de negocio que valida la tarjeta y gatilla el Feign de vuelta
        String resultado = bancoService.procesarTransaccionBancaria(dto);

        // Retorna un JSON limpio con el mensaje de éxito y el estado del procesamiento
        return ResponseEntity.ok(Map.of(
                "status", "PROCESADO",
                "detalle", resultado
        ));
    }

    @PostMapping("/notificaciones/enviar-alerta")
    public ResponseEntity<String> enviarAlertaViaBanco(@RequestBody Object dto) {
        try {
            notificacionClient.enviar(dto);
            return ResponseEntity.ok("Alerta procesada por el Proxy del Banco y enviada a Notificaciones.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al contactar con el servicio de notificaciones: " + e.getMessage());
        }
    }

}