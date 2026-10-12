package com.unas.grassbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 🟢 IMPORTANTE
import jakarta.annotation.PostConstruct; // Importación para el bloque de inicio
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling //  ENCIENDE EL ROBOT
public class GrassBackendApplication {

    // Método que se ejecuta justo al arrancar la aplicación
    @PostConstruct
    public void init() {
        // Sincroniza el servidor con la hora de Perú
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    public static void main(String[] args) {
        SpringApplication.run(GrassBackendApplication.class, args);
    }
}