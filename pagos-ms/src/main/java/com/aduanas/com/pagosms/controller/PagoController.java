package com.aduanas.com.pagosms.controller;

import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
import com.aduanas.com.pagosms.dto.NotificacionBancoDto;
import com.aduanas.com.pagosms.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pagos", description = "Gestión de recaudación y Webhooks bancarios")
@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
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
    public ResponseEntity<List<PagoResponseDto>> listarTodos() {
        return ResponseEntity.ok(pagoService.obtenerTodosLosPagos());
    }

    @Operation(summary = "Ver orden de pago", description = "Consulta el estado de la recaudación")
    @GetMapping("/{id}")
    public EntityModel<PagoResponseDto> buscar(@PathVariable Long id) {
        PagoResponseDto dto = pagoService.obtenerPagoPorId(id);
        return EntityModel.of(dto,
                linkTo(methodOn(PagoController.class).buscar(id)).withSelfRel(),
                Link.of("http://localhost:8080/api/v1/cargas/" + dto.getIdCarga()).withRel("ver_carga_liberada")
        );
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