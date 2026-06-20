package com.aduanas.com.pagosms.service;

import com.aduanas.com.pagosms.Enum.EstadoCarga;
import com.aduanas.com.pagosms.client.ClasificacionFeignClient;
import com.aduanas.com.pagosms.dto.CargaExternaDto;
import com.aduanas.com.pagosms.dto.PagoRequestDto;
import com.aduanas.com.pagosms.dto.PagoResponseDto;
import com.aduanas.com.pagosms.entity.Pago;
import com.aduanas.com.pagosms.repository.PagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Activa Mockito para simular dependencias
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ClasificacionFeignClient clasificacionClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    @DisplayName("Prueba de Tramo 1: Peso entre 1000kg y 1999kg")
    void testCalculoTramo1() {
        // 1. ARRANGE (Preparar el escenario)
        CargaExternaDto cargaSimulada = new CargaExternaDto();
        cargaSimulada.setPeso(new BigDecimal("1500.00")); // Peso de Tramo 1
        cargaSimulada.setEstado(EstadoCarga.PENDIENTE_PAGO);
        cargaSimulada.setMontoImpuesto(new BigDecimal("10000.00"));

        // Simulamos que Clasificación devuelve la carga y el Repositorio guarda el pago
        when(clasificacionClient.obtenerCargaPorId(anyLong())).thenReturn(cargaSimulada);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArguments()[0]);

        PagoRequestDto request = new PagoRequestDto(1L, new BigDecimal("10.00"), "CLP", "TX-123");

        // 2. ACT (Ejecutar la lógica)
        PagoResponseDto resultado = pagoService.procesarPago(request);

        // 3. ASSERT (Verificar resultados)
        // El Cargo Fijo esperado para este tramo es $50.000 CLP
        assertEquals("$1.000.000 CLP", resultado.getCargoFijo());
    }

    @Test
    @DisplayName("Caso 2: Carga no está en PENDIENTE_PAGO - Debe lanzar excepción")
    void testEstadoInvalido() {
        // ARRANGE: Carga en estado REGISTRADA (No se puede pagar todavía)
        CargaExternaDto cargaFalsa = new CargaExternaDto();
        cargaFalsa.setEstado(EstadoCarga.REGISTRADA);

        when(clasificacionClient.obtenerCargaPorId(anyLong())).thenReturn(cargaFalsa);
        PagoRequestDto request = new PagoRequestDto(1L, BigDecimal.ZERO, "CLP", "TX-ERR");

        // ACT & ASSERT: Verificamos que el código EXPLOTE con el mensaje correcto
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pagoService.procesarPago(request);
        });

        assertTrue(exception.getMessage().contains("Error de estado"));
    }
}