package com.aduanas.comex.riesgo_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF para permitir POST desde Postman/Feign
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Bloquea to do acceso sin login
                )
                .httpBasic(Customizer.withDefaults()); // Activa el cuadro de login de Basic Auth

        return http.build();
    }
}