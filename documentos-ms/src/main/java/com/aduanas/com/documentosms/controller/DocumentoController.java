package com.aduanas.com.documentosms.controller;

import com.aduanas.com.documentosms.dto.DocumentoRequestAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoRequestUsuarioExDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseUsuarioExDTO;
import com.aduanas.com.documentosms.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Documentos", description = "Gestión de facturas y cumplimiento legal")
@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor // Inyecta el Service automáticamente al compilar
public class DocumentoController {

    private final DocumentoService documentoService;

    // ==========================================
    // POST: CLIENTE SUBE DOCUMENTO (FLUJO 1)
    // ==========================================
    @PostMapping("/cliente")
    public ResponseEntity<DocumentoResponseUsuarioExDTO> clienteSubeDocumento(
            @Valid @RequestBody DocumentoRequestUsuarioExDTO dto) {

        // Retorna un 201 Created (Corregido el comentario que decía 211)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoService.clienteSubeDocumento(dto));
    }

    // ==========================================
    // GET: BUSCAR POR ID
    // ==========================================
    @Operation(summary = "Consultar documento", description = "Ver estado de validación de la factura")
    @GetMapping("/{id}")
    public EntityModel<DocumentoResponseAnalistaDTO> buscar(@PathVariable Long id) {
        DocumentoResponseAnalistaDTO dto = documentoService.buscarPorId(id);
        return EntityModel.of(dto,
                linkTo(methodOn(DocumentoController.class).buscar(id)).withSelfRel()
        );
    }

    // ==========================================
    // GET: LISTAR TODOS (Agregado para dar soporte completo al listar() del Service)
    // ==========================================
    @GetMapping
    public ResponseEntity<List<DocumentoResponseAnalistaDTO>> listar() {
        return ResponseEntity.ok(documentoService.listar());
    }

    // ==========================================
    // PUT: ANALISTA REVISA DOCUMENTO
    // ==========================================
    @PutMapping("/analista/{id}")
    public ResponseEntity<DocumentoResponseAnalistaDTO> analistaRevisaDocumento(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoRequestAnalistaDTO dto) {

        // ✅ CORREGIDO: El Service ya devuelve el DTO directo o lanza la excepción si no existe.
        DocumentoResponseAnalistaDTO response = documentoService.analistaRevisaDocumento(id, dto);
        return ResponseEntity.ok(response);
    }
}