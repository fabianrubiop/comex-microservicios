package com.aduanas.com.pagosms.client;

import com.aduanas.com.pagosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "carga-ms", url = "http://localhost:8081/api/v1/cargas")
public interface CargaFeignClient {

    @GetMapping("/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);

    // ¡FALTABA ESTE COHETE EN TU CÓDIGO! Para poder liberar la carga vía PUT
    @PutMapping("/{id}/actualizar-estado")
    void actualizarEstadoCarga(@PathVariable("id") Long id, @RequestParam("estado") String estado);
}

