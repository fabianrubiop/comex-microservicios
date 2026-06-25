package com.aduanas.comex.cargams.client;

import com.aduanas.comex.cargams.dto.external.ClasificacionRequestDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 🎯 CORREGIDO: Se quitó el url="..." para que busque a través de Eureka usando el nombre del servicio
@FeignClient(name = "clasificacion-ms")
public interface ClasificacionClient {

    @PostMapping("/api/v1/clasificaciones/evaluar/{idCarga}")
    ClasificacionResponseDTO evaluar(
            @PathVariable("idCarga") Long idCarga,
            @RequestBody ClasificacionRequestDTO request
    );

    @GetMapping("/api/v1/clasificaciones/{idClasificacion}")
    ClasificacionResponseDTO obtenerClasificacionPorId(
            @PathVariable("idClasificacion") Long idClasificacion
    );
}