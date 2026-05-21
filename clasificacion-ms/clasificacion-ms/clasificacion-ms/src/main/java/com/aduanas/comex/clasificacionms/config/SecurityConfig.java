package com.aduanas.comex.clasificacionms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // DESACTIVA CSRF
                .csrf(csrf -> csrf.disable())

                // PERMITE TODOS LOS ENDPOINTS
                .authorizeHttpRequests(auth ->

                        auth.anyRequest()
                                .permitAll()
                );

        return http.build();
    }
}