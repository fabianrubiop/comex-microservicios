package com.aduanas.comex.cargams.service;

import com.aduanas.comex.cargams.client.ClasificacionClient;
import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CrearCargaResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.repository.CargaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CargaService {

    private final CargaRepository cargaRepository;
    private final ClasificacionClient clasificacionClient;

    public CrearCargaResponseDTO crear(CrearCargaRequestDTO dto){

        Carga carga = Carga.builder().nroDeclaracion(dto.getNroDeclaracion()).descripcion(dto.getDescripcion()).paisOrigen(dto.getPaisOrigen()).valorDeclarado(dto.getValorDeclarado()).importadorRut(dto.getImportadorRut()).estado(EstadoCarga.REGISTRADA).fechaCreacion(LocalDateTime.now()).build();

        carga = cargaRepository.save(carga);



    }


}
