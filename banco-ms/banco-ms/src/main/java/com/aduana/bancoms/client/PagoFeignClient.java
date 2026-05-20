package com.aduana.bancoms.client;

    import com.aduana.bancoms.dto.NotificacionBancoDto;
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;

    // Conectamos directamente con el puerto 8083 de tu pagos-ms
    @FeignClient(name = "pagos-ms", url = "http://localhost:8083/api/pagos")
    public interface PagoFeignClient {

    // Le pega al endpoint receptor de tu módulo de pagos
    @PostMapping("/confirmar-banco")
    void enviarConfirmacionAlMicroservicioPagos(@RequestBody NotificacionBancoDto dto);
}