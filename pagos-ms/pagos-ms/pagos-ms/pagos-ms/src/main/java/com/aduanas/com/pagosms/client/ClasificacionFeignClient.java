package com.aduanas.com.pagosms.client;

import com.aduanas.com.pagosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// CORREGIDO: Dejamos la URL base limpia en la raíz del microservicio destino (puerto 8082)
// para mapear las rutas de forma explícita en cada método sin solapamientos.
@FeignClient(name = "clasificacion-ms", url = "http://localhost:8082")
public interface ClasificacionFeignClient {

    @GetMapping("/api/clasificaciones/cargas/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);

    @PutMapping("/api/clasificaciones/{cargaId}/liberar")
    void actualizarEstadoLiberacion(
            @PathVariable("cargaId") Long cargaId,
            @RequestParam("estadoAduanero") String estadoAduanero
    );
}