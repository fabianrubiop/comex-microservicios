package com.aduanas.comex.clasificacionms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "documento-ms", url = "http://localhost:8083/api/v1/documentos") // Ajusta el puerto que uses
public interface DocumentoClient {
    @PostMapping("/generar-din")
    void generarDeclaracionIngreso(
            @RequestParam("cargaId") Long cargaId,
            @RequestParam("montoImpuesto") java.math.BigDecimal montoImpuesto,
            @RequestParam("rutImportador") String rutImportador
    );
}
