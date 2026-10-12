package com.unas.grassbackend.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    // ⚠️ TU TOKEN DE DECOLECTA (AQUÍ ESTÁ SEGURO)
    private final String TOKEN_DECOLECTA = "sk_15300.qmOmZhUqPJ4sMAzggK6G7JEpuIvcbdke";

    @GetMapping("/dni/{numero}")
    public ResponseEntity<?> consultarDNI(@PathVariable String numero) {
        try {
            String url = "https://api.decolecta.com/v1/reniec/dni?numero=" + numero;
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + TOKEN_DECOLECTA);
            headers.set("Accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento no encontrado");
        }
    }

    @GetMapping("/ruc/{numero}")
    public ResponseEntity<?> consultarRUC(@PathVariable String numero) {
        try {
            String url = "https://api.decolecta.com/v1/sunat/ruc?numero=" + numero;
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + TOKEN_DECOLECTA);
            headers.set("Accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empresa no encontrada");
        }
    }
}
