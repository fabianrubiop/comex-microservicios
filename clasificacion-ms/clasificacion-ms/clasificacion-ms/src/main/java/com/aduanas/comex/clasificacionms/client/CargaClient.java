package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "carga-ms", url = "http://localhost:8081/api/v1/cargas")
public interface CargaClient {

    @PutMapping("/{id}/asignar-impuesto")
    void asignarImpuestoYEstado(
            @PathVariable("id") Long id,
            @RequestParam("impuesto") BigDecimal impuesto,
            @RequestParam("estado") String estado
    );
}