package com.fly.company.f4u_backend.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class DebugControllerTest {

    @InjectMocks
    private DebugController debugController;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetTokenInfo_NoAuth() {
        ResponseEntity<?> response = debugController.getTokenInfo(null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    public void testGetTokenInfo_WithHeader() {
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test";
        ResponseEntity<?> response = debugController.getTokenInfo(token);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    public void testPublicEndpoint() {
        ResponseEntity<?> response = debugController.publicEndpoint();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("message"));
    }
}
