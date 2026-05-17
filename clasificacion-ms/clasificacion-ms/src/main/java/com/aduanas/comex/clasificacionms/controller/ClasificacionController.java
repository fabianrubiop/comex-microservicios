package com.aduanas.comex.clasificacionms.controller;

import com.aduanas.comex.clasificacionms.dto.ClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clasificaciones")
@RequiredArgsConstructor
public class ClasificacionController {

    private final ClasificacionService clasificacionService;

    @PostMapping("/evaluar")
    public ResponseEntity<ClasificacionResponseDTO> evaluar(
            @Valid @RequestBody ClasificacionRequestDTO dto) {
        return ResponseEntity.ok(clasificacionService.evaluar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ClasificacionResponseDTO>> listar(){
        return ResponseEntity.ok(clasificacionService.listar());
    }

    @GetMapping("/{id}")
    ResponseEntity<ClasificacionResponseDTO> obtenerPorId(@PathVariable Long clasificacionId){
        return ResponseEntity.ok(clasificacionService.obtenerPorId(clasificacionId));
    }

}
