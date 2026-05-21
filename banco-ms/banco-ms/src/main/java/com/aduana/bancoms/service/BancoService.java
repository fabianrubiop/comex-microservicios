package com.aduana.bancoms.service;

import com.aduana.bancoms.client.PagoFeignClient;
import com.aduana.bancoms.dto.NotificacionBancoDto;
import com.aduana.bancoms.dto.TransaccionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service // <-- Le avisa a Spring Boot que registre esta clase en el contenedor
@RequiredArgsConstructor // <-- Genera el constructor para inyectar el PagoFeignClient automáticamente
public class BancoService {

    private final PagoFeignClient pagoFeignClient;

    public String procesarTransaccionBancaria(TransaccionRequestDto dto) {

        // 1. SIMULACIÓN: Si la tarjeta empieza con "4444", el banco la rechaza (Estado FALLIDO en la aduana)
        boolean fondosSuficientes = !dto.getNumeroTarjeta().startsWith("4444");

        // 2. GENERADOR DE VÁUCHER: Creamos un código de autorización único que jamás se va a repetir
        String voucherId = "VOUCHER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. CONSTRUCCIÓN DE LA RESPUESTA: Armamos el paquete que necesita recibir pagos-ms
        NotificacionBancoDto notificacion = new NotificacionBancoDto();
        notificacion.setPagoId(dto.getPagoId());
        notificacion.setCargaId(dto.getCargaId());
        notificacion.setTransaccionExternalId(voucherId);

        // Aquí decidimos el destino del Enum usando el booleano
        notificacion.setTransaccionExitosa(fondosSuficientes);

        // 4. EL DISPARO DE FEIGN: El banco llama a pagos-ms en vivo para notificar la transacción
        try {
            pagoFeignClient.enviarConfirmacionAlMicroservicioPagos(notificacion);
        } catch (Exception e) {
            // Si el microservicio de pagos está caído, el banco frena todo por seguridad
            throw new RuntimeException("El banco procesó el dinero, pero el sistema de Aduanas no responde: " + e.getMessage());
        }

        // 5. RESPUESTA FINAL PARA POSTMAN
        if (!fondosSuficientes) {
            throw new RuntimeException("Transacción RECHAZADA por el banco. Fondos insuficientes en la tarjeta.");
        }

        return "Transacción APROBADA con éxito. Código de autorización: " + voucherId;
    }
}