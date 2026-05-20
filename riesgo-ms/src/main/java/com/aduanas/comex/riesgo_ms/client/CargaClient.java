package com.aduanas.comex.riesgo_ms.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "carga-ms", url = "http://localhost:8081")
public interface CargaClient {

    // ======================================================
    // ============== BUSCAR CARGA POR ID ===================
    // ======================================================
    //
    // Este endpoint llamará:
    //
    // GET http://localhost:8081/cargas/1
    //
    // desde riesgo-ms.
    //
    @GetMapping("/cargas/{id}")
    Object obtenerCargaPorId(@PathVariable Long id);
}