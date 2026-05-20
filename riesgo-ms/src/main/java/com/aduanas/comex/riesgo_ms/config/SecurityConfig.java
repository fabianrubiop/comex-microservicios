package com.aduanas.comex.riesgo_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {http
                // DESACTIVA CSRF
                .csrf(csrf -> csrf.disable())
                // PERMITE TODO
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}