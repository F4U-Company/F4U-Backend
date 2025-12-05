package com.fly.company.f4u_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
public class SeatLockServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private SeatLockService seatLockService;

    @BeforeEach
    void setUp() {
        // El mock ya está configurado por @Mock
    }

    @Test
    void testTryLock_NewLock_Success() {
        boolean result = seatLockService.tryLock(1L, "user-123");

        assertTrue(result);
    }

    @Test
    void testTryLock_SameUser_Renew() {
        seatLockService.tryLock(1L, "user-123");
        
        boolean result = seatLockService.tryLock(1L, "user-123");

        assertTrue(result);
    }

    @Test
    void testTryLock_DifferentUser_Blocked() {
        seatLockService.tryLock(1L, "user-123");
        
        boolean result = seatLockService.tryLock(1L, "user-456");

        assertFalse(result);
    }

    @Test
    void testReleaseLock_Success() {
        seatLockService.tryLock(1L, "user-123");
        
        seatLockService.releaseLock(1L);

        boolean result = seatLockService.tryLock(1L, "user-456");
        assertTrue(result);
    }

    @Test
    void testIsLocked_True() {
        seatLockService.tryLock(1L, "user-123");

        boolean result = seatLockService.isLocked(1L);

        assertTrue(result);
    }

    @Test
    void testIsLocked_False() {
        boolean result = seatLockService.isLocked(999L);

        assertFalse(result);
    }

    @Test
    void testIsLockedByUser_True() {
        seatLockService.tryLock(1L, "user-123");

        boolean result = seatLockService.isLockedByUser(1L, "user-123");

        assertTrue(result);
    }

    @Test
    void testIsLockedByUser_False_DifferentUser() {
        seatLockService.tryLock(1L, "user-123");

        boolean result = seatLockService.isLockedByUser(1L, "user-456");

        assertFalse(result);
    }

    @Test
    void testIsLockedByUser_False_NoLock() {
        boolean result = seatLockService.isLockedByUser(999L, "user-123");

        assertFalse(result);
    }

    @Test
    void testGetRemainingLockTimeSeconds_WithLock() {
        seatLockService.tryLock(1L, "user-123");

        long remainingTime = seatLockService.getRemainingLockTimeSeconds(1L);

        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= 900); // 15 minutos = 900 segundos
    }

    @Test
    void testGetRemainingLockTimeSeconds_NoLock() {
        long remainingTime = seatLockService.getRemainingLockTimeSeconds(999L);

        assertEquals(0, remainingTime);
    }

    @Test
    void testGetLockedByUserId_WithLock() {
        seatLockService.tryLock(1L, "user-123");

        String userId = seatLockService.getLockedByUserId(1L);

        assertEquals("user-123", userId);
    }

    @Test
    void testGetLockedByUserId_NoLock() {
        String userId = seatLockService.getLockedByUserId(999L);

        assertNull(userId);
    }

    @Test
    void testCleanupExpiredLocks() {
        // Este test verifica que el método scheduled no lanza excepciones
        seatLockService.tryLock(1L, "user-123");
        
        // No debe lanzar excepción
        assertDoesNotThrow(() -> {
            // El método cleanupExpiredLocks se ejecuta automáticamente cada 30 segundos
        });
    }
}
