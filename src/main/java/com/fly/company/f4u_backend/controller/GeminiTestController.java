package com.fly.company.f4u_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/test-gemini")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class GeminiTestController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @GetMapping
    public ResponseEntity<?> testGeminiConnection() {
        try {
            System.out.println("[TEST] Testing Gemini AI connection");
            System.out.println("[TEST] URL: " + geminiApiUrl);
            System.out.println("[TEST] API Key configured: " + (geminiApiKey != null && !geminiApiKey.isEmpty()));
            System.out.println("[TEST] API Key starts with: " + (geminiApiKey != null ? geminiApiKey.substring(0, Math.min(10, geminiApiKey.length())) : "null"));

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // Construir payload simple
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gemini-2.0-flash-exp");
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 0.7);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "Eres un asistente útil."));
            messages.add(Map.of("role", "user", "content", "Di 'hola' en español."));
            requestBody.put("messages", messages);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + geminiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("[TEST] Enviando petición...");

            // Hacer la llamada
            ResponseEntity<String> response = restTemplate.exchange(
                geminiApiUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
            );

            System.out.println("[TEST] Status code: " + response.getStatusCode());
            System.out.println("[TEST] Response body: " + response.getBody());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("statusCode", response.getStatusCode().value());
            result.put("response", objectMapper.readTree(response.getBody()));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("[TEST] ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("errorClass", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(error);
        }
    }
}
