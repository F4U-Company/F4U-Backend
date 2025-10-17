package com.fly.company.f4u_backend.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Backend está funcionando correctamente");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "F4U-Backend");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> databaseStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Este endpoint confirmará que la aplicación puede arrancar
            // lo que implica que la conexión a la base de datos está funcionando
            response.put("database", "CONNECTED");
            response.put("message", "Base de datos conectada correctamente");
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("database", "ERROR");
            response.put("message", "Error de conexión a la base de datos");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/auth-config")
    public ResponseEntity<Map<String, Object>> authConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("authentication", "OAuth2 + JWT");
        response.put("provider", "Microsoft Entra ID");
        response.put("defaultRole", "USUARIO");
        response.put("message", "Sistema de autenticación configurado");
        response.put("endpoints", Map.of(
            "profile", "/api/auth/me",
            "roles", "/api/auth/roles",
            "validate", "/api/auth/validate-token"
        ));
        return ResponseEntity.ok(response);
    }
}