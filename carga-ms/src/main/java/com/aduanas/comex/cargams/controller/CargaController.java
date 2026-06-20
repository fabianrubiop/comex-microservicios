package com.aduanas.comex.cargams.controller;

import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.service.CargaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Cargas", description = "Gestión de la Fuente de Verdad de mercancías")
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
    public ResponseEntity<List<CargaResponseDTO>> listar() {
        return ResponseEntity.ok(cargaService.listar());
    }

    @Operation(summary = "Consultar carga por ID", description = "Retorna datos de la carga con navegación HATEOAS")
    @GetMapping("/{idCarga}")
    public EntityModel<CargaResponseDTO> obtenerPorId(@PathVariable Long idCarga) {
        CargaResponseDTO dto = cargaService.obtenerPorId(idCarga);
        return EntityModel.of(dto,
                linkTo(methodOn(CargaController.class).obtenerPorId(idCarga)).withSelfRel(),
                Link.of("http://localhost:8080/api/v1/clasificaciones/cargas/" + idCarga).withRel("ver_impuestos")
        );
    }

    // Metodo interno que usaran otros servicios para actualizar estado
    @PostMapping("/{idCarga}/actualizar-estado")
    public void actualizarEstado(@PathVariable(name = "idCarga") Long idCarga, @RequestParam String estado, @RequestParam BigDecimal impuesto, @RequestParam(name = "voucher", required = false) String voucher) {
        cargaService.actualizarImpuestoYEstado(idCarga, impuesto, com.aduanas.comex.cargams.enums.EstadoCarga.valueOf(estado), voucher);
    }

    @DeleteMapping("/{idCarga}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idCarga) {
        cargaService.eliminarCarga(idCarga);
        return ResponseEntity.noContent().build(); // Retorna 204 (Éxito sin contenido)
    }
}