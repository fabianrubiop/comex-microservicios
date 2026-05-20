package com.aduanas.com.documentosms.client;

import com.aduanas.com.documentosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Apunta al microservicio destino que calcula los impuestos y maneja el estado aduanero inmediato
@FeignClient(name = "clasificacion-ms", url = "http://localhost:8082")
public interface ClasificacionFeignClient {

    // Documentos solo necesita CONSULTAR (GET) los datos de la carga para validar carpetas
    @GetMapping("/api/clasificaciones/cargas/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);
}