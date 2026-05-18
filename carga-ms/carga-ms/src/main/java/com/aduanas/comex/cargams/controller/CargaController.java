package com.aduanas.comex.cargams.controller;


import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.service.CargaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargas")
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
}