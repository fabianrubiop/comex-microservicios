package com.aduanas.comex.riesgo_ms.controller;

import com.aduanas.comex.riesgo_ms.dto.RiesgoRequestDTO;
import com.aduanas.comex.riesgo_ms.dto.RiesgoResponseDTO;
import com.aduanas.comex.riesgo_ms.service.RiesgoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Riesgo", description = "Análisis de inteligencia aduanera")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    // ✅ CONECTADO: Mapeo exacto de parámetros URL para el cliente Feign de clasificación
    @GetMapping("/evaluar")
    public ResponseEntity<Boolean> evaluarRiesgoPolitico(
            @RequestParam String rut,
            @RequestParam String pais
    ) {
        boolean tieneRiesgo = riesgoService.evaluarRiesgoPolitico(rut, pais);
        return ResponseEntity.ok(tieneRiesgo);
    }

    @PostMapping
    public ResponseEntity<RiesgoResponseDTO> crear(@Valid @RequestBody RiesgoRequestDTO dto) {
        RiesgoResponseDTO respuesta = riesgoService.crear(dto);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @Operation(summary = "Evaluar riesgo", description = "POST manual para forzar semáforo aduanero")
    @PostMapping("/evaluar/{idCarga}")
    public EntityModel<RiesgoResponseDTO> evaluarCarga(@PathVariable Long idCarga) {
        RiesgoResponseDTO dto = riesgoService.evaluarCarga(idCarga);
        return EntityModel.of(dto,
                linkTo(methodOn(RiesgoController.class).evaluarCarga(idCarga)).withSelfRel()
        );
    }

    @GetMapping
    public ResponseEntity<List<RiesgoResponseDTO>> listar() {
        return ResponseEntity.ok(riesgoService.listar());
    }

    @GetMapping("/{idRiesgo}")
    public ResponseEntity<RiesgoResponseDTO> buscarPorId(@PathVariable Long idRiesgo) {
        return ResponseEntity.ok(riesgoService.buscarPorId(idRiesgo));
    }

    @GetMapping("/canal/{canal}")
    public ResponseEntity<List<RiesgoResponseDTO>> buscarPorCanal(@PathVariable String canal) {
        return ResponseEntity.ok(riesgoService.buscarPorCanal(canal));
    }

    @GetMapping("/carga/{idCarga}")
    public ResponseEntity<List<RiesgoResponseDTO>> buscarPorCarga(@PathVariable Long idCarga) {
        return ResponseEntity.ok(riesgoService.buscarPorCarga(idCarga));
    }

    @PutMapping("/{idRiesgo}")
    public ResponseEntity<RiesgoResponseDTO> actualizar(@PathVariable Long idRiesgo, @Valid @RequestBody RiesgoRequestDTO dto) {
        return ResponseEntity.ok(riesgoService.actualizar(idRiesgo, dto));
    }

    @DeleteMapping("/{idRiesgo}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idRiesgo) {
        riesgoService.eliminar(idRiesgo);
        return ResponseEntity.noContent().build();
    }
}