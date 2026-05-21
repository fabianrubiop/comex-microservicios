package com.aduanas.comex.clasificacionms.client;

import com.aduanas.comex.clasificacionms.enums.EstadoCarga; // El import del Enum que creamos
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "carga-ms", url = "http://localhost:8081")
public interface CargaClient {

    @PutMapping("/api/v1/cargas/{id}/asignar-impuesto")
    void actualizarImpuestoYEstado(
            @PathVariable("id") Long id,
            @RequestParam("impuesto") BigDecimal impuesto,
            @RequestParam("estado") EstadoCarga estado // <-- AQUÍ CAMBIA: De String a EstadoCarga
    );
}