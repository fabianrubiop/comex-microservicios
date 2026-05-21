package com.aduanas.comex.clasificacionms.controller;


import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // GET: http://localhost:8082/api/clasificaciones/cargas/%7BcargaId%7D
    @GetMapping("/cargas/{cargaId}")
    public ResponseEntity<ClasificacionResponseDTO> obtenerPorCargaId(@PathVariable Long cargaId) {
        return ResponseEntity.ok(clasificacionService.obtenerPorCargaId(cargaId));
    }

    @PutMapping("/{cargaId}/liberar")
    public ResponseEntity<Void> actualizarEstadoLiberacion(
            @PathVariable Long cargaId,
            @RequestParam String estadoAduanero) {

        // Aquí ellos buscan la clasificación y le cambian el estado en su base de datos
        System.out.println("¡Alerta Pagos! Cambiando carga " + cargaId + " a estado: " + estadoAduanero);

        return ResponseEntity.ok().build();
    }

}
