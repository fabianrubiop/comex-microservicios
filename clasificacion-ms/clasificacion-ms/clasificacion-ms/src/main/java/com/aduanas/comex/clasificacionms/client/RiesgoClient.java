package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "riesgo-ms", url = "http://localhost:8085/api/v1/riesgos")
public interface RiesgoClient {
    // Le preguntas a riesgos si el importador o la carga tienen alertas
    @GetMapping("/evaluar")
    boolean evaluarRiesgoCarga(
            @RequestParam("rut") String rut,
            @RequestParam("paisOrigen") String pais
    );
}
