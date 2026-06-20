package com.aduanas.comex.clasificacionms.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        // Inyecta automáticamente las credenciales en cada llamada entre microservicios
        return new BasicAuthRequestInterceptor("admin", "comex123");
    }
}