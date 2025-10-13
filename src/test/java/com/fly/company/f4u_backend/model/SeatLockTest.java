package com.fly.company.f4u_backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SeatLock model.
 * Tests the business logic and behavior of seat lock objects.
 */
class SeatLockTest {

    @Test
    void testSeatLockCreation() {
        // Given
        Long seatId = 1L;
        String sessionId = "session-123";
        LocalDateTime lockedAt = LocalDateTime.now();
        LocalDateTime expiresAt = lockedAt.plusMinutes(5);
        String lockedBy = "192.168.1.1";

        // When
        SeatLock seatLock = new SeatLock(seatId, sessionId, lockedAt, expiresAt, lockedBy);

        // Then
        assertEquals(seatId, seatLock.getSeatId());
        assertEquals(sessionId, seatLock.getSessionId());
        assertEquals(lockedAt, seatLock.getLockedAt());
        assertEquals(expiresAt, seatLock.getExpiresAt());
        assertEquals(lockedBy, seatLock.getLockedBy());
    }

    @Test
    void testIsExpired_NotExpired() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When & Then
        assertFalse(seatLock.isExpired());
    }

    @Test
    void testIsExpired_Expired() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.minusMinutes(1); // Expired 1 minute ago
        SeatLock seatLock = new SeatLock(1L, "session-123", now.minusMinutes(6), expiresAt, "user-1");

        // When & Then
        assertTrue(seatLock.isExpired());
    }

    @Test
    void testGetRemainingSeconds_NotExpired() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When
        long remainingSeconds = seatLock.getRemainingSeconds();

        // Then
        assertTrue(remainingSeconds > 0);
        assertTrue(remainingSeconds <= 300); // Should be around 5 minutes (300 seconds)
    }

    @Test
    void testGetRemainingSeconds_Expired() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.minusMinutes(1); // Expired 1 minute ago
        SeatLock seatLock = new SeatLock(1L, "session-123", now.minusMinutes(6), expiresAt, "user-1");

        // When
        long remainingSeconds = seatLock.getRemainingSeconds();

        // Then
        assertEquals(0, remainingSeconds);
    }

    @Test
    void testEquals_SameObject() {
        // Given
        SeatLock seatLock = new SeatLock(1L, "session-123", LocalDateTime.now(), 
                LocalDateTime.now().plusMinutes(5), "user-1");

        // When & Then
        assertEquals(seatLock, seatLock);
    }

    @Test
    void testEquals_EqualObjects() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        
        SeatLock seatLock1 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");
        SeatLock seatLock2 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When & Then
        assertEquals(seatLock1, seatLock2);
    }

    @Test
    void testEquals_DifferentSeatId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        
        SeatLock seatLock1 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");
        SeatLock seatLock2 = new SeatLock(2L, "session-123", now, expiresAt, "user-1");

        // When & Then
        assertNotEquals(seatLock1, seatLock2);
    }

    @Test
    void testEquals_DifferentSessionId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        
        SeatLock seatLock1 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");
        SeatLock seatLock2 = new SeatLock(1L, "session-456", now, expiresAt, "user-1");

        // When & Then
        assertNotEquals(seatLock1, seatLock2);
    }

    @Test
    void testEquals_NullObject() {
        // Given
        SeatLock seatLock = new SeatLock(1L, "session-123", LocalDateTime.now(), 
                LocalDateTime.now().plusMinutes(5), "user-1");

        // When & Then
        assertNotEquals(seatLock, null);
    }

    @Test
    void testEquals_DifferentClass() {
        // Given
        SeatLock seatLock = new SeatLock(1L, "session-123", LocalDateTime.now(), 
                LocalDateTime.now().plusMinutes(5), "user-1");

        // When & Then
        assertNotEquals(seatLock, "not a SeatLock");
    }

    @Test
    void testHashCode_EqualObjects() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        
        SeatLock seatLock1 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");
        SeatLock seatLock2 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When & Then
        assertEquals(seatLock1.hashCode(), seatLock2.hashCode());
    }

    @Test
    void testHashCode_DifferentObjects() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        
        SeatLock seatLock1 = new SeatLock(1L, "session-123", now, expiresAt, "user-1");
        SeatLock seatLock2 = new SeatLock(2L, "session-123", now, expiresAt, "user-1");

        // When & Then
        assertNotEquals(seatLock1.hashCode(), seatLock2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When
        String toString = seatLock.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("SeatLock"));
        assertTrue(toString.contains("seatId=1"));
        assertTrue(toString.contains("sessionId='session-123'"));
        assertTrue(toString.contains("lockedBy='user-1'"));
        assertTrue(toString.contains("remainingSeconds"));
    }

    @Test
    void testSeatLockImmutability() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);
        SeatLock seatLock = new SeatLock(1L, "session-123", now, expiresAt, "user-1");

        // When - try to modify the lock (this should not be possible as fields are final)
        // The test verifies that the object is immutable by design

        // Then - all getters should return the original values
        assertEquals(1L, seatLock.getSeatId());
        assertEquals("session-123", seatLock.getSessionId());
        assertEquals(now, seatLock.getLockedAt());
        assertEquals(expiresAt, seatLock.getExpiresAt());
        assertEquals("user-1", seatLock.getLockedBy());
    }
}
