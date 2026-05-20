package com.aduanas.comex.riesgo_ms.controller;


import com.aduanas.comex.riesgo_ms.dto.RiesgoRequestDTO;
import com.aduanas.comex.riesgo_ms.dto.RiesgoResponseDTO;
import com.aduanas.comex.riesgo_ms.service.RiesgoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/riesgos")
public class RiesgoController {

    private final RiesgoService service;

    @PostMapping
    public RiesgoResponseDTO crear(@Valid @RequestBody RiesgoRequestDTO dto) {
        return service.crear(dto);
    }

    @GetMapping
    public List<RiesgoResponseDTO> listar() {return service.listar();
    }

    @GetMapping("/{id}")
    public RiesgoResponseDTO buscarPorId(@PathVariable Long id) {return service.buscarPorId(id);
    }
    @GetMapping("/canal/{canal}")
    public List<RiesgoResponseDTO> buscarPorCanal(@PathVariable String canal) {return service.buscarPorCanal(canal);
    }

    @GetMapping("/carga/{cargaId}")
    public List<RiesgoResponseDTO> buscarPorCarga(@PathVariable Long cargaId) {
        return service.buscarPorCarga(cargaId);
    }

    @PutMapping("/{id}")
    public RiesgoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody RiesgoRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
    @PostMapping("/evaluar/{cargaId}")
    public RiesgoResponseDTO evaluarCarga(@PathVariable Long cargaId) {
        return service.evaluarCarga(cargaId);
    }
}