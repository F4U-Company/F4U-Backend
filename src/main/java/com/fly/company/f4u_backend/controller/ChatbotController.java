package com.fly.company.f4u_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.service.ChatbotService;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://thankful-moss-0d2f3560f.1.azurestaticapps.net"})
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * Endpoint para procesar preguntas del chatbot
     */
    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            // Obtener la pregunta del usuario
            String question = request.get("question");
            
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "La pregunta no puede estar vacía"
                ));
            }

            // Obtener el email del usuario autenticado
            String userEmail = jwt.getClaim("preferred_username");
            if (userEmail == null || userEmail.isEmpty()) {
                userEmail = jwt.getClaim("email");
            }
            
            if (userEmail == null || userEmail.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo obtener el email del usuario"
                ));
            }

            // Procesar la pregunta
            String answer = chatbotService.processQuestion(userEmail, question);

            // Retornar la respuesta
            Map<String, String> response = new HashMap<>();
            response.put("question", question);
            response.put("answer", answer);
            response.put("userEmail", userEmail);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error en ChatbotController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Error al procesar la pregunta: " + e.getMessage()
            ));
        }
    }

    /**
     * Endpoint de health check para el chatbot
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Chatbot Service"
        ));
    }
}
