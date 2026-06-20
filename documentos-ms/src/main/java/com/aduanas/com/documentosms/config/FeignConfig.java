package com.aduanas.com.documentosms.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        // Esto le da el "pasaporte" a Documentos para entrar a Carga-ms
        return new BasicAuthRequestInterceptor("admin", "comex123");
    }
}