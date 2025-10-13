package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SeatLockRequest DTO.
 * Tests serialization, deserialization, and validation.
 */
class SeatLockRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSeatLockRequestCreation() {
        // Given
        Long seatId = 1L;
        String sessionId = "session-123";
        String lockedBy = "192.168.1.1";

        // When
        SeatLockRequest request = new SeatLockRequest(seatId, sessionId, lockedBy);

        // Then
        assertEquals(seatId, request.getSeatId());
        assertEquals(sessionId, request.getSessionId());
        assertEquals(lockedBy, request.getLockedBy());
    }

    @Test
    void testSeatLockRequestDefaultConstructor() {
        // When
        SeatLockRequest request = new SeatLockRequest();

        // Then
        assertNull(request.getSeatId());
        assertNull(request.getSessionId());
        assertNull(request.getLockedBy());
    }

    @Test
    void testSeatLockRequestSetters() {
        // Given
        SeatLockRequest request = new SeatLockRequest();
        Long seatId = 1L;
        String sessionId = "session-123";
        String lockedBy = "192.168.1.1";

        // When
        request.setSeatId(seatId);
        request.setSessionId(sessionId);
        request.setLockedBy(lockedBy);

        // Then
        assertEquals(seatId, request.getSeatId());
        assertEquals(sessionId, request.getSessionId());
        assertEquals(lockedBy, request.getLockedBy());
    }

    @Test
    void testSeatLockRequestToString() {
        // Given
        SeatLockRequest request = new SeatLockRequest(1L, "session-123", "192.168.1.1");

        // When
        String toString = request.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("SeatLockRequest"));
        assertTrue(toString.contains("seatId=1"));
        assertTrue(toString.contains("sessionId='session-123'"));
        assertTrue(toString.contains("lockedBy='192.168.1.1'"));
    }

    @Test
    void testSeatLockRequestJsonSerialization() throws Exception {
        // Given
        SeatLockRequest request = new SeatLockRequest(1L, "session-123", "192.168.1.1");

        // When
        String json = objectMapper.writeValueAsString(request);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"seatId\":1"));
        assertTrue(json.contains("\"sessionId\":\"session-123\""));
        assertTrue(json.contains("\"lockedBy\":\"192.168.1.1\""));
    }

    @Test
    void testSeatLockRequestJsonDeserialization() throws Exception {
        // Given
        String json = "{\"seatId\":1,\"sessionId\":\"session-123\",\"lockedBy\":\"192.168.1.1\"}";

        // When
        SeatLockRequest request = objectMapper.readValue(json, SeatLockRequest.class);

        // Then
        assertEquals(1L, request.getSeatId());
        assertEquals("session-123", request.getSessionId());
        assertEquals("192.168.1.1", request.getLockedBy());
    }

    @Test
    void testSeatLockRequestJsonDeserializationWithNullValues() throws Exception {
        // Given
        String json = "{\"seatId\":null,\"sessionId\":null,\"lockedBy\":null}";

        // When
        SeatLockRequest request = objectMapper.readValue(json, SeatLockRequest.class);

        // Then
        assertNull(request.getSeatId());
        assertNull(request.getSessionId());
        assertNull(request.getLockedBy());
    }

    @Test
    void testSeatLockRequestJsonDeserializationWithMissingFields() throws Exception {
        // Given
        String json = "{}";

        // When
        SeatLockRequest request = objectMapper.readValue(json, SeatLockRequest.class);

        // Then
        assertNull(request.getSeatId());
        assertNull(request.getSessionId());
        assertNull(request.getLockedBy());
    }
}
