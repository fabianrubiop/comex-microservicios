package com.aduana.bancoms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// CORREGIDO: Se remueve el "exclude" de DataSourceAutoConfiguration ya que este microservicio
// no tiene dependencias de BD instaladas en su pom.xml. Spring Boot iniciará limpio por defecto.
@SpringBootApplication
@EnableFeignClients
public class BancoMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoMsApplication.class, args);
    }
}