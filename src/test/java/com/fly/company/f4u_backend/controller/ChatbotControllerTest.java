package com.fly.company.f4u_backend.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.fly.company.f4u_backend.service.ChatbotService;

@ExtendWith(MockitoExtension.class)
class ChatbotControllerTest {

    @Mock
    private ChatbotService chatbotService;

    @InjectMocks
    private ChatbotController chatbotController;

    private Jwt mockJwt;

    @BeforeEach
    void setUp() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");
        claims.put("preferred_username", "testuser@example.com");
        
        mockJwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );
    }

    @Test
    void testAskQuestion_Success() {
        Map<String, String> request = new HashMap<>();
        request.put("question", "¿Cuáles son mis reservas?");

        when(chatbotService.processQuestion(anyString(), anyString()))
                .thenReturn("Tienes 1 reserva confirmada");

        ResponseEntity<?> response = chatbotController.askQuestion(request, mockJwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(chatbotService).processQuestion(anyString(), eq("¿Cuáles son mis reservas?"));
    }

    @Test
    void testAskQuestion_EmptyQuestion() {
        Map<String, String> request = new HashMap<>();
        request.put("question", "");

        ResponseEntity<?> response = chatbotController.askQuestion(request, mockJwt);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(chatbotService, never()).processQuestion(anyString(), anyString());
    }

    @Test
    void testAskQuestion_NullQuestion() {
        Map<String, String> request = new HashMap<>();

        ResponseEntity<?> response = chatbotController.askQuestion(request, mockJwt);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(chatbotService, never()).processQuestion(anyString(), anyString());
    }

    @Test
    void testAskQuestion_ServiceException() {
        Map<String, String> request = new HashMap<>();
        request.put("question", "test question");

        when(chatbotService.processQuestion(anyString(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        ResponseEntity<?> response = chatbotController.askQuestion(request, mockJwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
