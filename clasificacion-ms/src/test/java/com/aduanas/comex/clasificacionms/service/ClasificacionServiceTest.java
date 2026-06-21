package com.aduanas.comex.clasificacionms.service;

import com.aduanas.comex.clasificacionms.client.*;
import com.aduanas.comex.clasificacionms.dto.*;
import com.aduanas.comex.clasificacionms.entity.Clasificacion;
import com.aduanas.comex.clasificacionms.repository.ClasificacionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClasificacionServiceTest {

    @Mock private ClasificacionRepository repository;
    @Mock private RiesgoClient riesgoClient;
    @Mock private CargaClient cargaClient;
    @Mock private PagoClient pagoClient;
    @Mock private DocumentoClient documentoClient;
    @Mock private NotificacionClient notificacionClient;

    @InjectMocks private ClasificacionService service;

    @Test
    @DisplayName("CP-02: Cálculo Tributario - Debe aplicar 6% arancel y 19% IVA")
    void testCalculoImpuestos() {
        // ARRANGE
        EvaluarClasificacionRequestDTO request = new EvaluarClasificacionRequestDTO();
        request.setIdCarga(1L);
        request.setValorDeclarado(new BigDecimal("10000.00")); // 10,000 USD
        request.setImportadorRut("123-k");

        // Simulamos que NO tiene riesgo
        when(riesgoClient.evaluarRiesgoCarga(anyString(), anyString())).thenReturn(false);
        when(repository.save(any(Clasificacion.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        ClasificacionResponseDTO response = service.evaluar(request);

        // ASSERT
        // Cálculo: Arancel(600) + IVA( (10000+600)*0.19 = 2014 ) = Total 2614.00
        BigDecimal totalEsperado = new BigDecimal("2614.00");

        // 🚩 PARA QUE FALLE: Cambia el totalEsperado a "2500.00"
        assertEquals(0, totalEsperado.compareTo(response.getMontoImpuesto()),
                "El impuesto calculado no coincide con la normativa chilena");
    }
}