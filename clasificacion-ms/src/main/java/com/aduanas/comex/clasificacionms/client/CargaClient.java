package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "carga-ms", url = "http://localhost:8081") // O la URL de tu carga-ms
public interface CargaClient {

    @PostMapping("/api/v1/cargas/{idCarga}/actualizar-estado") // ✅ CAMBIADO a POST
    void actualizarEstado(
            @PathVariable(name = "idCarga") Long idCarga, // ✅ Agregado (name = "idCarga")
            @RequestParam(name = "impuesto") BigDecimal impuesto,
            @RequestParam(name = "estado") String estado,
            @RequestParam(name = "voucher", required = false) String voucher // ✅ Permitimos que sea opcional
    );
}