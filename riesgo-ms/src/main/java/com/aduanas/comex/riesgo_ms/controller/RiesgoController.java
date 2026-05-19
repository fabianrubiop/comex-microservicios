package com.aduanas.comex.riesgo_ms.controller;

// ======================================================
// ===================== IMPORTS =========================
// ======================================================

// DTOs
import com.aduanas.comex.riesgo_ms.dto.RiesgoRequestDTO;
import com.aduanas.comex.riesgo_ms.dto.RiesgoResponseDTO;

// Service
import com.aduanas.comex.riesgo_ms.service.RiesgoService;

// Validaciones
import jakarta.validation.Valid;

// REST CONTROLLER
import org.springframework.web.bind.annotation.*;

// Listas
import java.util.List;

// ======================================================
// ================= REST CONTROLLER =====================
// ======================================================
//
// Esta clase expone endpoints REST.
//
// Permite recibir requests desde:
// - Postman
// - Frontend
// - Swagger
//
@RestController

// ======================================================
// URL BASE
// ======================================================
//
// Todos los endpoints comenzarán con:
//
// /riesgos
//
@RequestMapping("/riesgos")
public class RiesgoController {

    // ======================================================
    // ===================== SERVICE =========================
    // ======================================================
    //
    // El controller utiliza el service
    // para ejecutar lógica negocio.
    //
    // El controller NO debería
    // conectarse directo a MySQL.
    //
    private final RiesgoService service;

    // ======================================================
    // ===================== CONSTRUCTOR =====================
    // ======================================================
    //
    // Spring inyecta automáticamente
    // el service.
    //
    public RiesgoController(
            RiesgoService service
    ) {

        this.service = service;
    }

    // ======================================================
    // ===================== CREAR ===========================
    // ======================================================
    //
    // Endpoint:
    //
    // POST /riesgos
    //
    // Sirve para crear un riesgo.
    //
    @PostMapping

    public RiesgoResponseDTO crear(

            // ======================================================
            // @Valid
            // ======================================================
            //
            // Ejecuta validaciones del DTO.
            //
            // Ejemplo:
            //
            // @NotBlank
            // @Size
            //
            @Valid

            // ======================================================
            // @RequestBody
            // ======================================================
            //
            // Convierte JSON → Java.
            //
            @RequestBody

            RiesgoRequestDTO dto
    ) {

        // ======================================================
        // LLAMAR SERVICE
        // ======================================================
        //
        // El controller delega la lógica.
        //
        return service.crear(dto);
    }

    // ======================================================
    // ===================== LISTAR ==========================
    // ======================================================
    //
    // Endpoint:
    //
    // GET /riesgos
    //
    // Obtiene todos los riesgos.
    //
    @GetMapping
    public List<RiesgoResponseDTO> listar() {

        return service.listar();
    }

    // ======================================================
    // ===================== BUSCAR ID =======================
    // ======================================================
    //
    // Endpoint:
    //
    // GET /riesgos/1
    //
    // Busca riesgo por ID.
    //
    @GetMapping("/{id}")

    public RiesgoResponseDTO
    buscarPorId( @PathVariable Long id)

            // ======================================================
            // @PathVariable
            // ======================================================
            //
            // Obtiene valor desde URL.
            //
     {

        return service.buscarPorId(id);
    }

    // ======================================================
    // ===================== BUSCAR NIVEL ====================
    // ======================================================
    //
    // Endpoint:
    //
    // GET /riesgos/nivel/ALTO
    //
    // Busca riesgos por nivel.
    //
    @GetMapping("/nivel/{nivel}")

    public List<RiesgoResponseDTO> buscarPorNivel(@PathVariable String nivel) {

        return service.buscarPorNivel(nivel);
    }

    // ======================================================
    // ===================== ACTUALIZAR ======================
    // ======================================================
    //
    // Endpoint:
    //
    // PUT /riesgos/1
    //
    // Actualiza un riesgo existente.
    //
    @PutMapping("/{id}")

    public RiesgoResponseDTO actualizar(@PathVariable Long id, @Valid  @RequestBody  RiesgoRequestDTO dto)

            // ID URL = @PathVariable, Long id,

            // Validar DTO @Valid

            // JSON BODY @RequestBody
            {

        return service.actualizar(
                id,
                dto
        );
    }

    // ======================================================
    // ===================== ELIMINAR ========================
    // ======================================================
    //
    // Endpoint:
    //
    // DELETE /riesgos/1
    //
    // Elimina riesgo por ID.
    //
    @DeleteMapping("/{id}")

    public void eliminar(

            @PathVariable
            Long id
    ) {

        service.eliminar(id);
    }
}