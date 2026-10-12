package com.unas.grassbackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Genera una clave criptográfica súper segura automáticamente
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // El token durará 24 horas (en milisegundos)
    private static final long EXPIRATION_TIME = 86400000; 

    public String generateToken(String email, Integer idRol) {
        return Jwts.builder()
                .setSubject(email) // El "dueño" del token
                .claim("rol", idRol) // Guardamos su rol para saber qué permisos tiene
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de caducidad
                .signWith(SECRET_KEY) // Lo firmamos matemáticamente
                .compact(); // Lo empaquetamos en el String final
    }
}
