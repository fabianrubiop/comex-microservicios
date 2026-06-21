package com.aduanas.comex.cargams.config;

import com.aduanas.comex.cargams.entity.Carga;
import com.aduanas.comex.cargams.enums.EstadoCarga;
import com.aduanas.comex.cargams.repository.CargaRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private CargaRepository repository;

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        for (int i = 0; i < 5; i++) {
            repository.save(Carga.builder()
                    .numeroDeclaracion("DEC-" + faker.number().digits(5))
                    .descripcion(faker.commerce().productName())
                    .paisOrigen(faker.country().name())
                    .valorDeclarado(BigDecimal.valueOf(faker.number().randomDouble(2, 500, 50000)))
                    .peso(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 2000)))
                    .importadorRut(faker.number().digits(8) + "-k")
                    .estado(EstadoCarga.REGISTRADA)
                    .fechaCreacion(LocalDateTime.now())
                    .build());
        }
    }
}