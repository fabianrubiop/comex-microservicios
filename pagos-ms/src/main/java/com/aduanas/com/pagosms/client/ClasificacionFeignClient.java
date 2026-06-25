package com.aduanas.com.pagosms.client;

import com.aduanas.com.pagosms.Enum.EstadoCarga;
import com.aduanas.com.pagosms.dto.CargaExternaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "clasificacion-ms")
public interface ClasificacionFeignClient {

    // 1. Obtiene los datos de peso e impuesto desde el microservicio de clasificación
    @GetMapping("/api/v1/clasificaciones/cargas/{id}")
    CargaExternaDto obtenerCargaPorId(@PathVariable("id") Long id);

    @PostMapping("/api/v1/clasificaciones/{idCarga}/liberar")
    void actualizarEstadoLiberacion(
            @PathVariable("idCarga") Long idCarga,
            @RequestParam("estadoAduanero") String estadoAduanero,
            @RequestParam("voucher") String voucher
    );
}