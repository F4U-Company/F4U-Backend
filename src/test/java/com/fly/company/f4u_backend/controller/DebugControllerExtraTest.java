package com.fly.company.f4u_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebugControllerExtraTest {

    @InjectMocks
    private DebugController debugController;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Test
    void testGetTokenInfoWithNullAuthHeader() {
        ResponseEntity<?> response = debugController.getTokenInfo(null);
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    void testGetTokenInfoWithAuthHeader() {
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
        
        ResponseEntity<?> response = debugController.getTokenInfo(authHeader);
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("authHeaderPresent"));
        assertNotNull(body.get("authHeaderValue"));
    }

    @Test
    void testGetTokenInfoWithShortAuthHeader() {
        String authHeader = "Bearer short";
        
        ResponseEntity<?> response = debugController.getTokenInfo(authHeader);
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    void testGetTokenInfoWithEmptyAuthHeader() {
        ResponseEntity<?> response = debugController.getTokenInfo("");
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    void testPublicEndpoint() {
        ResponseEntity<?> response = debugController.publicEndpoint();
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Este endpoint es público y no requiere autenticación", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testPublicEndpointTimestamp() {
        ResponseEntity<?> response1 = debugController.publicEndpoint();
        ResponseEntity<?> response2 = debugController.publicEndpoint();
        
        Map<String, Object> body1 = (Map<String, Object>) response1.getBody();
        Map<String, Object> body2 = (Map<String, Object>) response2.getBody();
        
        assertNotNull(body1.get("timestamp"));
        assertNotNull(body2.get("timestamp"));
    }

    @Test
    void testPublicEndpointReturnsCorrectMessage() {
        ResponseEntity<?> response = debugController.publicEndpoint();
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("timestamp"));
        assertEquals(2, body.size());
    }

    @Test
    void testGetTokenInfoWithVeryLongAuthHeader() {
        String longHeader = "Bearer " + "x".repeat(1000);
        
        ResponseEntity<?> response = debugController.getTokenInfo(longHeader);
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("authHeaderPresent"));
    }

    @Test
    void testGetTokenInfoResponseStructure() {
        ResponseEntity<?> response = debugController.getTokenInfo("Bearer test");
        
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.containsKey("authHeaderPresent"));
        assertTrue(body.containsKey("authenticated"));
    }

    @Test
    void testGetTokenInfoMultipleTimes() {
        for (int i = 0; i < 5; i++) {
            ResponseEntity<?> response = debugController.getTokenInfo("Bearer token" + i);
            assertEquals(200, response.getStatusCodeValue());
        }
    }

    @Test
    void testPublicEndpointMultipleCalls() {
        ResponseEntity<?> r1 = debugController.publicEndpoint();
        ResponseEntity<?> r2 = debugController.publicEndpoint();
        ResponseEntity<?> r3 = debugController.publicEndpoint();
        
        assertEquals(200, r1.getStatusCodeValue());
        assertEquals(200, r2.getStatusCodeValue());
        assertEquals(200, r3.getStatusCodeValue());
    }

    @Test
    void testGetTokenInfoWithDifferentBearerFormats() {
        ResponseEntity<?> r1 = debugController.getTokenInfo("Bearer token123");
        ResponseEntity<?> r2 = debugController.getTokenInfo("bearer lowercase");
        ResponseEntity<?> r3 = debugController.getTokenInfo("BEARER UPPERCASE");
        
        assertEquals(200, r1.getStatusCodeValue());
        assertEquals(200, r2.getStatusCodeValue());
        assertEquals(200, r3.getStatusCodeValue());
    }
}
