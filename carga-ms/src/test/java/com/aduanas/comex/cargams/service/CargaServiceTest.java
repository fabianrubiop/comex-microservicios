package com.aduanas.comex.cargams.service;

import com.aduanas.comex.cargams.client.ClasificacionClient;
import com.aduanas.comex.cargams.dto.CrearCargaRequestDTO;
import com.aduanas.comex.cargams.dto.CargaResponseDTO;
import com.aduanas.comex.cargams.dto.external.ClasificacionResponseDTO;
import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.repository.CargaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargaServiceTest {

    @Mock private CargaRepository repository;
    @Mock private ClasificacionClient clasificacionClient;
    @InjectMocks private CargaService service;

    @Test
    @DisplayName("CP-01: Registro Exitoso - La carga debe quedar en estado REGISTRADA inicialmente")
    void crearCargaExito() {
        // ARRANGE
        CrearCargaRequestDTO dto = new CrearCargaRequestDTO();
        dto.setNumeroDeclaracion("DEC-001");
        dto.setValorDeclarado(new BigDecimal("1000"));
        dto.setImportadorRut("12345678-9");

        Carga cargaGuardada = Carga.builder().idCarga(1L).estado(EstadoCarga.REGISTRADA).build();
        ClasificacionResponseDTO respExterna = new ClasificacionResponseDTO();
        respExterna.setPermitido(true);

        when(repository.save(any(Carga.class))).thenReturn(cargaGuardada);
        when(clasificacionClient.evaluar(anyLong(), any())).thenReturn(respExterna);
        when(repository.findById(anyLong())).thenReturn(Optional.of(cargaGuardada));

        // ACT
        CargaResponseDTO result = service.crear(dto);

        // ASSERT
        assertNotNull(result);
        // 🚩 PARA QUE FALLE: Cambia "REGISTRADA" por "LIBERADA".
        assertEquals("REGISTRADA", result.getEstado());
        verify(repository, atLeastOnce()).save(any());
    }
}