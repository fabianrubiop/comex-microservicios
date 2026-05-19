package com.aduanas.comex.notificacion_ms.config;

// ======================================================
// ===================== IMPORTS =========================
// ======================================================

// CONFIGURACIÓN SPRING
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// SECURITY
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// FILTRO SEGURIDAD
import org.springframework.security.web.SecurityFilterChain;

// ======================================================
// ===================== CONFIG ==========================
// ======================================================
//
// Clase configuración global.
//
// Sirve para configurar
// Spring Security.
//
@Configuration
public class SecurityConfig {

    // ======================================================
    // ============ SECURITY FILTER CHAIN ===================
    // ======================================================
    //
    // Configura reglas seguridad.
    //
    @Bean
    public SecurityFilterChain securityFilterChain(

            HttpSecurity http
    ) throws Exception {

        http

                // ======================================================
                // DESACTIVAR CSRF
                // ======================================================
                //
                // CSRF protege formularios web.
                //
                // Como usamos REST API + Postman,
                // normalmente se desactiva.
                //
                .csrf(csrf -> csrf.disable())

                // ======================================================
                // PERMITIR REQUESTS
                // ======================================================
                //
                // Permite acceder
                // a todos los endpoints.
                //
                .authorizeHttpRequests(auth ->

                        auth.anyRequest()
                                .permitAll()
                );
        // ======================================================
        // CONSTRUIR CONFIG
        // ======================================================
        return http.build();
    }
}