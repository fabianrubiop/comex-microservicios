package com.aduanas.comex.cargams.client;

import com.aduanas.comex.cargams.dto.external.ClasificacionRequestDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "clasificacion-cumplimiento-ms",
        url = "${services.clasificacion.url}"
)
public interface ClasificacionClient {

    @PostMapping("/api/clasificaciones/evaluar")
    ClasificacionResponseDTO evaluar(@RequestBody ClasificacionRequestDTO request);
}
