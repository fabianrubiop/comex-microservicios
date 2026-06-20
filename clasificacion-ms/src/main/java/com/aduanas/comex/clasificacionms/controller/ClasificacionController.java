package com.aduanas.comex.clasificacionms.controller;

import com.aduanas.comex.clasificacionms.dto.ClasificacionResponseDTO;
import com.aduanas.comex.clasificacionms.dto.EvaluarClasificacionRequestDTO;
import com.aduanas.comex.clasificacionms.service.ClasificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Clasificación", description = "Cálculo tributario y arancelario")
@RestController
@RequestMapping("/api/v1/clasificaciones")
@RequiredArgsConstructor
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
    @Operation(summary = "Consultar liquidación", description = "Ver desglose de impuestos calculados")
    @GetMapping("/cargas/{idCarga}")
    public EntityModel<ClasificacionResponseDTO> obtenerPorCarga(@PathVariable Long idCarga) {
        ClasificacionResponseDTO dto = clasificacionService.obtenerPorCargaId(idCarga);
        return EntityModel.of(dto,
                linkTo(methodOn(ClasificacionController.class).obtenerPorCarga(idCarga)).withSelfRel(),
                Link.of("http://localhost:8080/api/v1/cargas/" + idCarga).withRel("volver_a_carga_maestra")
        );
    }

    @PostMapping("/{idCarga}/liberar")
    public ResponseEntity<Void> liberarCarga(@PathVariable(name = "idCarga") Long idCarga, @RequestParam String estadoAduanero, @RequestParam String voucher) {
        clasificacionService.liberarCargaEnSistema(idCarga, estadoAduanero, voucher);
        return ResponseEntity.ok().build();
    }
}