package com.fly.company.f4u_backend.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @Test
    public void testHealth() {
        ResponseEntity<Map<String, String>> response = healthController.health();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("F4U Backend", response.getBody().get("service"));
    }

    @Test
    public void testHome() {
        ResponseEntity<Map<String, String>> response = healthController.home();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("F4U Backend is running", response.getBody().get("message"));
        assertEquals("healthy", response.getBody().get("status"));
    }

    @Test
    public void testActuatorHealth() {
        ResponseEntity<Map<String, String>> response = healthController.actuatorHealth();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }
}
