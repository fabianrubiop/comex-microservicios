package com.aduana.bancoms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- Este import se agregará solo o ponlo tú

@EnableFeignClients // <--- ¡AQUÍ ESTÁ EL TRUCO, AGREGA ESTO!
@SpringBootApplication
public class BancoMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BancoMsApplication.class, args);
    }
}