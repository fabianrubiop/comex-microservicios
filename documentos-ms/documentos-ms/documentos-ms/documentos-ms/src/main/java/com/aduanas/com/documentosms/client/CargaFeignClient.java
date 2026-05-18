package com.aduanas.com.documentosms.client;

import com.aduanas.com.documentosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carga-ms", url = "http://localhost:8081/api/v1/cargas")
public interface CargaFeignClient {

    @GetMapping("/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);
}