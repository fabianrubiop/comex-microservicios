package com.aduanas.comex.notificacion_ms.controller;

import com.aduanas.comex.notificacion_ms.dto.NotificacionRequestDTO;
import com.aduanas.comex.notificacion_ms.dto.NotificacionResponseDTO;
import com.aduanas.comex.notificacion_ms.service.NotificacionService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(
            NotificacionService service
    ) {
        this.service = service;
    }

    // CREAR NOTIFICACIÓN
    @PostMapping
    public NotificacionResponseDTO crear(
            @Valid
            @RequestBody
            NotificacionRequestDTO dto
    ) {

        return service.crear(dto);
    }

    // LISTAR TODAS LAS NOTIFICACIONES
    @GetMapping
    public List<NotificacionResponseDTO> listar() {

        return service.listar();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public NotificacionResponseDTO buscarPorId(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    // BUSCAR POR ESTADO
    @GetMapping("/estado/{estado}")
    public List<NotificacionResponseDTO>
    buscarPorEstado(
            @PathVariable String estado
    ) {

        return service.buscarPorEstado(estado);
    }

    // ACTUALIZAR NOTIFICACIÓN
    @PutMapping("/{id}")
    public NotificacionResponseDTO actualizar(
            @PathVariable Long id,

            @Valid
            @RequestBody
            NotificacionRequestDTO dto
    ) {

        return service.actualizar(id, dto);
    }

    // ELIMINAR NOTIFICACIÓN
    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id
    ) {

        service.eliminar(id);
    }
}