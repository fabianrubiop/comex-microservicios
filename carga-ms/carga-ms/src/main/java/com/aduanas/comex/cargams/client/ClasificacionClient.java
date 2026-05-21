package com.aduanas.comex.cargams.client;

import com.aduanas.comex.cargams.dto.external.ClasificacionRequestDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "clasificacion-ms", url = "http://localhost:8082")
public interface ClasificacionClient {

    // CORREGIDO: Ahora incluye /{cargaId} en la URL y el @PathVariable para enlazarlo
    @PostMapping("/api/v1/clasificaciones/evaluar/{cargaId}")
    ClasificacionResponseDTO evaluar(
            @PathVariable("cargaId") Long cargaId,
            @RequestBody ClasificacionRequestDTO request
    );

    @GetMapping("/api/v1/clasificaciones/{id}")
    ResponseEntity<ClasificacionResponseDTO> obtenerClasificacionPorId(@PathVariable("id") Long id);
}