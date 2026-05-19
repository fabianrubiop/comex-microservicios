package com.aduanas.comex.clasificacionms.controller;


import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import com.aduanas.comex.clasificacionms.enums.TipoClasificacion;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clasificaciones")
@RequiredArgsConstructor
public class ClasificacionController {

    private final ClasificacionService clasificacionService;

    @PostMapping("/evaluar/{cargaId}")
    public ResponseEntity<ClasificacionResponseDTO> evaluar(
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

    @PostMapping("/procesar")
    public ResponseEntity<Clasificacion> procesarClasificacion(
            @RequestParam Long cargaId,
            @RequestParam BigDecimal valorDeclarado,
            @RequestParam(required = false) String observaciones,
            @RequestParam TipoClasificacion tipo) {

        Clasificacion resultado = clasificacionService.clasificarMercaderia(cargaId, valorDeclarado, observaciones, tipo);
        return ResponseEntity.ok(resultado);
    }
}
