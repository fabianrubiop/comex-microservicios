package com.aduana.bancoms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // <-- EXCLUIMOS LA BD AQUÍ
@EnableFeignClients

public class BancoMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoMsApplication.class, args);
	}

}
