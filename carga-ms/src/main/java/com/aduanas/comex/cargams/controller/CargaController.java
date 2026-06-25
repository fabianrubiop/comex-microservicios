package com.aduanas.comex.cargams.controller;

import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.service.CargaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cargas")
@RequiredArgsConstructor
@Tag(name = "Cargas", description = "Operaciones relacionadas con las Cargas")
public class CargaController {


    private final CargaService cargaService;


    @GetMapping
    @Operation(summary = "Obtener todas las cargas", description = "Obtiene una lista de todas las cargas")
    public ResponseEntity<List<CargaResponseDTO>> listar() {
        return ResponseEntity.ok(cargaService.listar());
    }

    @PostMapping
    public ResponseEntity<CargaResponseDTO> crear(
            @Valid @RequestBody CrearCargaRequestDTO dto) {
        return ResponseEntity.ok(cargaService.crear(dto));
    }

    @GetMapping("/{idCarga}")
    public ResponseEntity<CargaResponseDTO> obtenerPorId(@PathVariable Long idCarga) {
        return ResponseEntity.ok(cargaService.obtenerPorId(idCarga));
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