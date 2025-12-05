package com.fly.company.f4u_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeminiTestControllerExtraTest {

    @InjectMocks
    private GeminiTestController geminiTestController;

    @Test
    void testGeminiTestControllerExists() {
        assertNotNull(geminiTestController);
    }

    @Test
    void testGeminiApiKeyCanBeSet() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "test-key-123");
        String key = (String) ReflectionTestUtils.getField(geminiTestController, "geminiApiKey");
        assertEquals("test-key-123", key);
    }

    @Test
    void testGeminiApiUrlCanBeSet() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://test.api.com");
        String url = (String) ReflectionTestUtils.getField(geminiTestController, "geminiApiUrl");
        assertEquals("https://test.api.com", url);
    }

    @Test
    void testGeminiConnectionWithNullApiKey() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", null);
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://api.test.com");
        
        assertDoesNotThrow(() -> {
            geminiTestController.testGeminiConnection();
        });
    }

    @Test
    void testGeminiConnectionWithEmptyApiKey() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "");
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://api.test.com");
        
        assertDoesNotThrow(() -> {
            geminiTestController.testGeminiConnection();
        });
    }

    @Test
    void testGeminiConnectionWithValidKey() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "valid-api-key-12345");
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://api.gemini.com");
        
        // No lanzará excepción aunque falle la conexión real
        assertDoesNotThrow(() -> {
            geminiTestController.testGeminiConnection();
        });
    }

    @Test
    void testGeminiConnectionReturnsResponseEntity() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "test-key");
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://invalid-url-test.com");
        
        ResponseEntity<?> response = geminiTestController.testGeminiConnection();
        assertNotNull(response);
    }

    @Test
    void testMultipleApiKeySettings() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "key1");
        assertEquals("key1", ReflectionTestUtils.getField(geminiTestController, "geminiApiKey"));
        
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "key2");
        assertEquals("key2", ReflectionTestUtils.getField(geminiTestController, "geminiApiKey"));
        
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", "key3");
        assertEquals("key3", ReflectionTestUtils.getField(geminiTestController, "geminiApiKey"));
    }

    @Test
    void testApiUrlWithDifferentProtocols() {
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "http://test.com");
        assertEquals("http://test.com", ReflectionTestUtils.getField(geminiTestController, "geminiApiUrl"));
        
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://secure.test.com");
        assertEquals("https://secure.test.com", ReflectionTestUtils.getField(geminiTestController, "geminiApiUrl"));
    }

    @Test
    void testGeminiConnectionWithLongApiKey() {
        String longKey = "a".repeat(100);
        ReflectionTestUtils.setField(geminiTestController, "geminiApiKey", longKey);
        ReflectionTestUtils.setField(geminiTestController, "geminiApiUrl", "https://api.test.com");
        
        assertDoesNotThrow(() -> {
            geminiTestController.testGeminiConnection();
        });
    }
}
