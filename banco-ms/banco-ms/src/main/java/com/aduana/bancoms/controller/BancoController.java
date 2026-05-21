package com.aduana.bancoms.controller;

import com.aduana.bancoms.dto.TransaccionRequestDto;
import com.aduana.bancoms.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/banco")
@RequiredArgsConstructor // <-- Inyecta el BancoService automáticamente usando Lombok
public class BancoController {

    private final BancoService bancoService;

    // POST: http://localhost:8086/api/banco/procesar
    @PostMapping("/procesar")
    public ResponseEntity<Map<String, String>> pagarCargaAduanera(@RequestBody TransaccionRequestDto dto) {
        // Llama a la lógica de negocio que valida la tarjeta y gatilla el Feign de vuelta
        String resultado = bancoService.procesarTransaccionBancaria(dto);

        // Retorna un JSON limpio con el mensaje de éxito y el número de váucher
        return ResponseEntity.ok(Map.of("mensaje", resultado));
    }
}
