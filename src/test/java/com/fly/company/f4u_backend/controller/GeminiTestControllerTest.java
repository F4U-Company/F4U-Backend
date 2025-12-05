package com.fly.company.f4u_backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GeminiTestControllerTest {

    @InjectMocks
    private GeminiTestController geminiTestController;

    @Test
    public void testControllerInitialization() {
        assertNotNull(geminiTestController);
    }

    @Test
    public void testApiKeyConfiguration() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "test-key");
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://test.api.com");
        
        String apiKey = (String) ReflectionTestUtils.getField(geminiTestController, "geminiApiKey");
        String apiUrl = (String) ReflectionTestUtils.getField(geminiTestController, "geminiApiUrl");
        
        assertEquals("test-key", apiKey);
        assertEquals("https://test.api.com", apiUrl);
    }
}
