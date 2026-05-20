package com.aduanas.com.documentosms.controller;

import com.aduanas.com.documentosms.dto.DocumentoRequestAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoRequestUsuarioExDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseAnalistaDTO;
import com.aduanas.com.documentosms.dto.DocumentoResponseUsuarioExDTO;
import com.aduanas.com.documentosms.entity.Documento;
import com.aduanas.com.documentosms.repository.DocumentoRepository;
import com.aduanas.com.documentosms.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor // <-- Inyecta el Service automáticamente estilo profesional
public class DocumentoController {

    private final DocumentoService documentoService;

    // ==========================================
    // POST: CLIENTE SUBE DOCUMENTO (FLUJO 1)
    // ==========================================
    @PostMapping("/cliente")
    public ResponseEntity<DocumentoResponseUsuarioExDTO> clienteSubeDocumento(
            @Valid @RequestBody DocumentoRequestUsuarioExDTO dto) {

        // Retorna un 211 Created con la respuesta mapeada
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentoService.clienteSubeDocumento(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseAnalistaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.buscarPorId(id));
    }

    // ==========================================
    // PUT: ANALISTA REVISA DOCUMENTO
    // ==========================================
    @PutMapping("/analista/{id}")
    public ResponseEntity<DocumentoResponseAnalistaDTO> analistaRevisaDocumento(
            @PathVariable Long id,
            @Valid @RequestBody DocumentoRequestAnalistaDTO dto) {

        // Usamos el .map() para retornar 200 OK si existe, o lanzar RuntimeException si no existe
        return documentoService.analistaRevisaDocumento(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("No se encontró el documento con ID: " + id));
    }
}