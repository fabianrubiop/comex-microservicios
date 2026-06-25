package com.aduanas.com.pagosms.controller;

import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
import com.aduanas.com.pagosms.dto.NotificacionBancoDto;
import com.aduanas.com.pagosms.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name ="Pagos",description = "Operaciones relacionadas con los Pagos")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponseDto> registrarPago(@Valid @RequestBody PagoRequestDto requestDto) {
        return new ResponseEntity<>(pagoService.procesarPago(requestDto), HttpStatus.CREATED);
    }

    @PostMapping("/notificacion-banco")
    public ResponseEntity<PagoResponseDto> recibirNotificacionBanco(@Valid @RequestBody NotificacionBancoDto bancoDto) {
        return ResponseEntity.ok(pagoService.confirmarPagoDesdeBanco(bancoDto));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los pagos", description = "Obtiene una lista de todos los pagos")
    public ResponseEntity<List<PagoResponseDto>> listarTodos() {
        return ResponseEntity.ok(pagoService.obtenerTodosLosPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPagoPorId(id));
    }

    @PostMapping("/crear-orden")
    public ResponseEntity<Void> crearOrdenDesdeClasificacion(
            @RequestParam("idCarga") Long idCarga,
            @RequestParam("monto") java.math.BigDecimal monto) {

        PagoRequestDto dto = PagoRequestDto.builder()
                .idCarga(idCarga)
                .monto(monto)
                .moneda("CLP")
                .idTransaccionExterna("PENDIENTE-" + idCarga)
                .build();

        pagoService.procesarPago(dto);
        return ResponseEntity.ok().build();
    }
}