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


    //Sirve para: Traer un listado completo con el historial de todas las clasificaciones de aduana que se han hecho en el sistema.
    //Por qué importa: Te devuelve una lista de ClasificacionResponseDTO, lo que significa que cuando tires este GET en Postman,
    // vas a ver todas las cargas evaluadas con sus respectivos montos de impuesto, observaciones y si fueron permitidas o no.
    // Es ideal para que un administrador vea el panorama completo.
}
