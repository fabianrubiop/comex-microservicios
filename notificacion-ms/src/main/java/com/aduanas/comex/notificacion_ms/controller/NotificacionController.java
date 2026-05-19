package com.aduanas.comex.notificacion_ms.controller;

import com.aduanas.comex.notificacion_ms.dto.NotificacionRequestDTO;
import com.aduanas.comex.notificacion_ms.dto.NotificacionResponseDTO;
import com.aduanas.comex.notificacion_ms.service.NotificacionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // CREAR NOTIFICACIÓN
    @PostMapping
    public NotificacionResponseDTO crear(
            @Valid
            @RequestBody
            NotificacionRequestDTO dto
    ) {

        return notificacionService.crear(dto);
    }

    // LISTAR TODAS LAS NOTIFICACIONES
    @GetMapping
    public List<NotificacionResponseDTO> listar() {

        return notificacionService.listar();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public NotificacionResponseDTO buscarPorId(
            @PathVariable Long id
    ) {

        return notificacionService.buscarPorId(id);
    }

    // BUSCAR POR ESTADO
    @GetMapping("/estado/{estado}")
    public List<NotificacionResponseDTO>
    buscarPorEstado(
            @PathVariable String estado
    ) {

        return notificacionService.buscarPorEstado(estado);
    }

    // ACTUALIZAR NOTIFICACIÓN
    @PutMapping("/{id}")
    public NotificacionResponseDTO actualizar(
            @PathVariable Long id,

            @Valid
            @RequestBody
            NotificacionRequestDTO dto
    ) {

        return notificacionService.actualizar(id, dto);
    }

    // ELIMINAR NOTIFICACIÓN
    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id
    ) {

        notificacionService.eliminar(id);
    }

    @PostMapping("/enviar-alerta")
    public ResponseEntity<String> recibirAlerta(@RequestParam String email, @RequestParam String mensaje) {

        // Llama al servicio as铆ncrono (no se queda esperando el resultado)
        notificacionService.enviarEmailAsincrono(email, mensaje);

        // Retorna inmediatamente un 200 OK a Clasificaci贸n
        return ResponseEntity.ok("Notificaci贸n recibida y proces谩ndose en segundo plano.");
    }
}