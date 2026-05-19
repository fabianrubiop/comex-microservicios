package com.aduanas.com.pagosms.controller;

import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
import com.aduanas.com.pagosms.dto.NotificacionBancoDto; // El nuevo DTO para el banco
import com.aduanas.com.pagosms.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    // 🖥️ PANTALLA USUARIO EXTERNO: El cliente inicia el flujo y el pago queda PENDIENTE/PROCESANDO
    @PostMapping
    public ResponseEntity<PagoResponseDto> registrarPago(@Valid @RequestBody PagoRequestDto requestDto) {
        PagoResponseDto respuesta = pagoService.procesarPago(requestDto);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // 🏦 SIMULADOR DEL BANCO: El endpoint que "escucha" si la plata entró para pasar a COMPLETADO y liberar
    @PostMapping("/notificacion-banco")
    public ResponseEntity<PagoResponseDto> recibirNotificacionBanco(@Valid @RequestBody NotificacionBancoDto bancoDto) {
        // El service se encarga de cambiar a COMPLETADO y pegarle al microservicio de Cargas
        PagoResponseDto respuesta = pagoService.confirmarPagoDesdeBanco(bancoDto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<PagoResponseDto>> listarTodos() {
        List<PagoResponseDto> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDto> buscarPorId(@PathVariable Long id) {
        PagoResponseDto pago = pagoService.obtenerPagoPorId(id);
        return ResponseEntity.ok(pago);
    }
}