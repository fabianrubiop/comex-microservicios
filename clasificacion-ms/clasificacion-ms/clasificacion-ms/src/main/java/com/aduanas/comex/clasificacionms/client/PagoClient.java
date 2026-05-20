package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "pago-ms", url = "http://localhost:8084")
public interface PagoClient {
    @PostMapping("/crear-orden")
    void crearOrdenDePago(
            @RequestParam("cargaId") Long cargaId,
            @RequestParam("montoA Pagar") java.math.BigDecimal monto
    );
}
