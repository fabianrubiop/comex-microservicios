package com.aduanas.com.pagosms.client;

import com.aduanas.com.pagosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "clasificacion-ms", url = "http://localhost:8082/api/clasificaciones")
public interface ClasificacionFeignClient {

    @GetMapping("/cargas/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);

    // ¡AQUÍ ES DONDE VA EL PUT! Pagos es el único que gatilla la liberación en "APROBADO"
    @PutMapping("/{cargaId}/liberar")
    void actualizarEstadoLiberacion(
            @PathVariable("cargaId") Long cargaId,
            @RequestParam("estadoAduanero") String estadoAduanero
    );
}