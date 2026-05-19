package com.aduanas.comex.cargams.controller;


import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.service.CargaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cargas")
@RequiredArgsConstructor
public class CargaController {

    private final CargaService cargaService;

    @PostMapping
    public ResponseEntity<CargaResponseDTO> crear(
            @Valid @RequestBody CrearCargaRequestDTO dto) {
        return ResponseEntity.ok(cargaService.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<Carga>> listar() {
        return ResponseEntity.ok(cargaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carga> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargaService.obtenerPorId(id));
    }


    @PutMapping("/{id}/asignar-impuesto")
    public ResponseEntity<Void> asignarImpuestoYEstado(
            @PathVariable Long id,
            @RequestParam java.math.BigDecimal impuesto,
            @RequestParam String estado) {

        // ¡Mucho más limpio y legible!
        EstadoCarga estadoEnum = EstadoCarga.valueOf(estado.toUpperCase());

        cargaService.actualizarImpuestoYEstado(id, impuesto, estadoEnum);

        return ResponseEntity.ok().build();
    }
}