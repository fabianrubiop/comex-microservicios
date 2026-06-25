package com.aduanas.comex.cargams.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.aduanas.comex.cargams.controller.CargaControlerV2;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CargaModelAssembler implements RepresentationModelAssembler<CargaResponseDTO, EntityModel<CargaResponseDTO>> {

    @Override
    public EntityModel<CargaResponseDTO> toModel(CargaResponseDTO carga) {
        return EntityModel.of(carga,
                // Agregamos una coma (,) al final de esta línea para separar los links:
                linkTo(methodOn(CargaControlerV2.class).obtenerPorId(carga.getIdCarga())).withSelfRel(),

                // Ahora este enlace ya no tirará error de compilación
                linkTo(methodOn(CargaControlerV2.class).listarHateoas()).withRel("cargas")
        );
    }
}