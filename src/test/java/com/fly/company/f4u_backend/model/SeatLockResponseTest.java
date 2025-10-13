package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SeatLockResponse DTO.
 * Tests serialization, deserialization, and response construction.
 */
class SeatLockResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSeatLockResponseCreation_Simple() {
        // Given
        boolean success = true;
        String message = "Seat locked successfully";

        // When
        SeatLockResponse response = new SeatLockResponse(success, message);

        // Then
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getSeatId());
        assertNull(response.getSessionId());
        assertNull(response.getLockedAt());
        assertNull(response.getExpiresAt());
        assertEquals(0, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseCreation_WithSeatLock() {
        // Given
        boolean success = true;
        String message = "Seat locked successfully";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "192.168.1.1");

        // When
        SeatLockResponse response = new SeatLockResponse(success, message, seatLock);

        // Then
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(1L, response.getSeatId());
        assertEquals("session-123", response.getSessionId());
        assertEquals(now, response.getLockedAt());
        assertEquals(expiresAt, response.getExpiresAt());
        assertTrue(response.getRemainingSeconds() > 0);
    }

    @Test
    void testSeatLockResponseCreation_WithNullSeatLock() {
        // Given
        boolean success = true;
        String message = "Seat locked successfully";

        // When
        SeatLockResponse response = new SeatLockResponse(success, message, null);

        // Then
        assertEquals(success, response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getSeatId());
        assertNull(response.getSessionId());
        assertNull(response.getLockedAt());
        assertNull(response.getExpiresAt());
        assertEquals(0, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseDefaultConstructor() {
        // When
        SeatLockResponse response = new SeatLockResponse();

        // Then
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getSeatId());
        assertNull(response.getSessionId());
        assertNull(response.getLockedAt());
        assertNull(response.getExpiresAt());
        assertEquals(0, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseSetters() {
        // Given
        SeatLockResponse response = new SeatLockResponse();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        // When
        response.setSuccess(true);
        response.setMessage("Test message");
        response.setSeatId(1L);
        response.setSessionId("session-123");
        response.setLockedAt(now);
        response.setExpiresAt(expiresAt);
        response.setRemainingSeconds(300);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals(1L, response.getSeatId());
        assertEquals("session-123", response.getSessionId());
        assertEquals(now, response.getLockedAt());
        assertEquals(expiresAt, response.getExpiresAt());
        assertEquals(300, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseToString() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "192.168.1.1");
        SeatLockResponse response = new SeatLockResponse(true, "Seat locked successfully", seatLock);

        // When
        String toString = response.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("SeatLockResponse"));
        assertTrue(toString.contains("success=true"));
        assertTrue(toString.contains("message='Seat locked successfully'"));
        assertTrue(toString.contains("seatId=1"));
        assertTrue(toString.contains("sessionId='session-123'"));
        assertTrue(toString.contains("remainingSeconds"));
    }

    @Test
    void testSeatLockResponseJsonDeserialization() throws Exception {
        // Given
        String json = "{\"success\":true,\"message\":\"Seat locked successfully\",\"seatId\":1,\"sessionId\":\"session-123\",\"remainingSeconds\":300}";

        // When
        SeatLockResponse response = objectMapper.readValue(json, SeatLockResponse.class);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Seat locked successfully", response.getMessage());
        assertEquals(1L, response.getSeatId());
        assertEquals("session-123", response.getSessionId());
        assertEquals(300, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseJsonDeserializationWithNullValues() throws Exception {
        // Given
        String json = "{\"success\":false,\"message\":\"Seat is not locked\",\"seatId\":null,\"sessionId\":null,\"lockedAt\":null,\"expiresAt\":null,\"remainingSeconds\":0}";

        // When
        SeatLockResponse response = objectMapper.readValue(json, SeatLockResponse.class);

        // Then
        assertFalse(response.isSuccess());
        assertEquals("Seat is not locked", response.getMessage());
        assertNull(response.getSeatId());
        assertNull(response.getSessionId());
        assertNull(response.getLockedAt());
        assertNull(response.getExpiresAt());
        assertEquals(0, response.getRemainingSeconds());
    }

    @Test
    void testSeatLockResponseFailureResponse() {
        // Given
        boolean success = false;
        String message = "Seat is already locked by another session";

        // When
        SeatLockResponse response = new SeatLockResponse(success, message);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getSeatId());
        assertNull(response.getSessionId());
        assertNull(response.getLockedAt());
        assertNull(response.getExpiresAt());
        assertEquals(0, response.getRemainingSeconds());
    }
}
