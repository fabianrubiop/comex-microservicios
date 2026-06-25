package com.aduanas.comex.clasificacionms.controller;

import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clasificaciones")
@RequiredArgsConstructor
@Tag(name = "Clasificaciones", description = "Operaciones relacionadas con las Clasificaciones")
public class ClasificacionController {

    private final ClasificacionService clasificacionService;

    // 1. EVALUAR UNA CARGA (✅ SOLUCIONADO: Cambiado a idCarga para hacer match con el Feign de Cargas)
    @PostMapping("/evaluar/{idCarga}")
    public ResponseEntity<ClasificacionResponseDTO> evaluar(
            @PathVariable Long idCarga,
            @Valid @RequestBody EvaluarClasificacionRequestDTO dto) {

        // Forzamos que el ID de la URL sea el mismo del DTO por consistencia
        dto.setIdCarga(idCarga);
        return ResponseEntity.ok(clasificacionService.evaluar(dto));
    }

    // 2. LISTAR TODAS LAS EVALUACIONES
    @Operation(summary = "Obtener todas las cargas" , description = "Obtiene la lista todas las clasificaciones")
    @GetMapping
    public ResponseEntity<List<ClasificacionResponseDTO>> listar() {
        return ResponseEntity.ok(clasificacionService.listar());
    }

    // 3. OBTENER POR ID DE CLASIFICACIÓN
    @GetMapping("/{idClasificacion}")
    public ResponseEntity<ClasificacionResponseDTO> obtenerPorId(@PathVariable Long idClasificacion) {
        return ResponseEntity.ok(clasificacionService.obtenerPorId(idClasificacion));
    }

    // 4. OBTENER LA ÚLTIMA CLASIFICACIÓN DE UNA CARGA ESPECÍFICA (✅ SOLUCIONADO: idCarga unificado)
    @GetMapping("/cargas/{idCarga}")
    public ResponseEntity<ClasificacionResponseDTO> obtenerPorCargaId(@PathVariable Long idCarga) {
        return ResponseEntity.ok(clasificacionService.obtenerPorCargaId(idCarga));
    }

    @PostMapping("/{idCarga}/liberar")
    public ResponseEntity<Void> liberarCarga(@PathVariable(name = "idCarga") Long idCarga, @RequestParam String estadoAduanero, @RequestParam String voucher) {
        clasificacionService.liberarCargaEnSistema(idCarga, estadoAduanero, voucher);
        return ResponseEntity.ok().build();
    }
}