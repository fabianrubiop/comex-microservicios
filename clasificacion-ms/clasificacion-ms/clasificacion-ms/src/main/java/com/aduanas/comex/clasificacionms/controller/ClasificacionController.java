package com.aduanas.comex.clasificacionms.controller;

import com.aduanas.comex.clasificacionms.client.CargaClient; // <-- Agregado el import del cliente Feign
import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/clasificaciones")
@RequiredArgsConstructor
public class ClasificacionController {

    private final ClasificacionService clasificacionService;
    private final CargaClient cargaClient; // <-- Inyectado aquí para poder usarlo en la liberación automática

    @PostMapping("/evaluar/{cargaId}")
    public ResponseEntity<ClasificacionResponseDTO> evaluar(
            @PathVariable("cargaId") Long cargaId,
            @Valid @RequestBody EvaluarClasificacionRequestDTO dto) {
        return ResponseEntity.ok(clasificacionService.evaluar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClasificacionResponseDTO>> listar() {
        return ResponseEntity.ok(clasificacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasificacionResponseDTO> obtenerPorId(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(clasificacionService.obtenerPorId(id));
    }

    @GetMapping("/cargas/{cargaId}")
    public ResponseEntity<ClasificacionResponseDTO> obtenerPorCargaId(@PathVariable Long cargaId) {
        return ResponseEntity.ok(clasificacionService.obtenerPorCargaId(cargaId));
    }

    @PutMapping("/{cargaId}/liberar")
    public ResponseEntity<Void> actualizarEstadoLiberacion(
            @PathVariable Long cargaId,
            @RequestParam String estadoAduanero) {

        log.info("¡Pago confirmado! Liberando carga ID: {} con estado: {}", cargaId, estadoAduanero);

        // Parseamos el string que manda pagos al Enum formal de tus 6 estados
        com.aduanas.comex.clasificacionms.enums.EstadoCarga nuevoEstado =
                com.aduanas.comex.clasificacionms.enums.EstadoCarga.valueOf(estadoAduanero.toUpperCase().trim());

        // Usamos el cliente Feign para cambiar el estado en tu carga-ms a LIBERADA automáticamente.
        // Pasamos null en el impuesto para que mantenga el Arancel+IVA que ya habíamos calculado.
        cargaClient.actualizarImpuestoYEstado(cargaId, null, nuevoEstado);

        return ResponseEntity.ok().build();
    }
}