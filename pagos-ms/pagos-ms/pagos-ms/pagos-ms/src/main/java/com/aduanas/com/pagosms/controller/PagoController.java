package com.aduanas.com.pagosms.controller;

import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
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

    // Endpoint para registrar y procesar un pago de aduanas
    @PostMapping
    public ResponseEntity<PagoResponseDto> registrarPago(@Valid @RequestBody PagoRequestDto requestDto) {
        PagoResponseDto respuesta = pagoService.procesarPago(requestDto);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
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
