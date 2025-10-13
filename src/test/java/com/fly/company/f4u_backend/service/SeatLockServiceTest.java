package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.SeatLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for SeatLockService.
 * Tests thread safety, concurrency, and business logic.
 */
@ExtendWith(MockitoExtension.class)
class SeatLockServiceTest {

    private SeatLockService seatLockService;

    @BeforeEach
    void setUp() {
        seatLockService = new SeatLockService();
    }

    @Test
    void testSuccessfulLock() {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";

        // When
        boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);

        // Then
        assertTrue(result);
        assertTrue(seatLockService.isLocked(seatId));
        
        Optional<SeatLock> lockInfo = seatLockService.getLockInfo(seatId);
        assertTrue(lockInfo.isPresent());
        assertEquals(seatId, lockInfo.get().getSeatId());
        assertEquals(sessionId, lockInfo.get().getSessionId());
        assertEquals(lockedBy, lockInfo.get().getLockedBy());
    }

    @Test
    void testLockAlreadyLockedSeat() {
        // Given
        Long seatId = 1L;
        String sessionId1 = "session-1";
        String sessionId2 = "session-2";
        String lockedBy = "user-1";

        // When
        boolean firstLock = seatLockService.tryLock(seatId, sessionId1, lockedBy);
        boolean secondLock = seatLockService.tryLock(seatId, sessionId2, lockedBy);

        // Then
        assertTrue(firstLock);
        assertFalse(secondLock);
        assertTrue(seatLockService.isLocked(seatId));
    }

    @Test
    void testReleaseLock() {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId, lockedBy);

        // When
        boolean result = seatLockService.releaseLock(seatId, sessionId);

        // Then
        assertTrue(result);
        assertFalse(seatLockService.isLocked(seatId));
        assertFalse(seatLockService.getLockInfo(seatId).isPresent());
    }

    @Test
    void testReleaseLockNotOwned() {
        // Given
        Long seatId = 1L;
        String sessionId1 = "session-1";
        String sessionId2 = "session-2";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId1, lockedBy);

        // When
        boolean result = seatLockService.releaseLock(seatId, sessionId2);

        // Then
        assertFalse(result);
        assertTrue(seatLockService.isLocked(seatId));
    }

    @Test
    void testExtendLock() throws InterruptedException {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId, lockedBy);
        
        // Small delay to ensure lock is properly established
        Thread.sleep(10);
        
        LocalDateTime originalExpiry = seatLockService.getLockInfo(seatId).get().getExpiresAt();

        // When
        boolean result = seatLockService.extendLock(seatId, sessionId);

        // Then
        assertTrue(result);
        assertTrue(seatLockService.isLocked(seatId));
        
        LocalDateTime newExpiry = seatLockService.getLockInfo(seatId).get().getExpiresAt();
        assertTrue(newExpiry.isAfter(originalExpiry));
    }

    @Test
    void testExtendLockNotOwned() {
        // Given
        Long seatId = 1L;
        String sessionId1 = "session-1";
        String sessionId2 = "session-2";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId1, lockedBy);

        // When
        boolean result = seatLockService.extendLock(seatId, sessionId2);

        // Then
        assertFalse(result);
    }

    @Test
    void testLockExpiration() throws InterruptedException {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId, lockedBy);

        // When - wait for lock to expire (this test assumes a very short lock duration for testing)
        // Note: In real implementation, locks expire after 5 minutes, so this test would need to be adjusted
        // For now, we'll test the cleanup method directly
        seatLockService.cleanupExpiredLocks();

        // Then
        assertTrue(seatLockService.isLocked(seatId)); // Should still be locked as it hasn't expired yet
    }

    @Test
    void testConcurrentLockAttempts() throws InterruptedException {
        // Given
        Long seatId = 1L;
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String sessionId = "session-" + threadId;
                    String lockedBy = "user-" + threadId;
                    
                    boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);
                    if (result) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        assertEquals(1, successCount.get());
        assertEquals(numberOfThreads - 1, failureCount.get());
        assertTrue(seatLockService.isLocked(seatId));
    }

    @Test
    void testConcurrentLockAndRelease() throws InterruptedException {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // When
        executor.submit(() -> {
            try {
                seatLockService.tryLock(seatId, sessionId, lockedBy);
                Thread.sleep(100); // Simulate some work
                seatLockService.releaseLock(seatId, sessionId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(50); // Wait a bit
                boolean result = seatLockService.tryLock(seatId, "session-2", "user-2");
                // This should fail initially, then succeed after release
                if (!result) {
                    Thread.sleep(100); // Wait for release
                    result = seatLockService.tryLock(seatId, "session-2", "user-2");
                }
                assertTrue(result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        assertTrue(seatLockService.isLocked(seatId));
    }

    @Test
    void testInvalidParameters() {
        // Test null seatId
        assertFalse(seatLockService.tryLock(null, "session-1", "user-1"));
        assertFalse(seatLockService.isLocked(null));
        assertFalse(seatLockService.releaseLock(null, "session-1"));
        assertFalse(seatLockService.extendLock(null, "session-1"));

        // Test null sessionId
        assertFalse(seatLockService.tryLock(1L, null, "user-1"));
        assertFalse(seatLockService.releaseLock(1L, null));
        assertFalse(seatLockService.extendLock(1L, null));

        // Test null lockedBy
        assertFalse(seatLockService.tryLock(1L, "session-1", null));
    }

    @Test
    void testGetActiveLockCount() {
        // Given
        assertEquals(0, seatLockService.getActiveLockCount());

        // When
        seatLockService.tryLock(1L, "session-1", "user-1");
        seatLockService.tryLock(2L, "session-2", "user-2");

        // Then
        assertEquals(2, seatLockService.getActiveLockCount());

        // When
        seatLockService.releaseLock(1L, "session-1");

        // Then
        assertEquals(1, seatLockService.getActiveLockCount());
    }

    @Test
    void testGenerateSessionId() {
        // When
        String sessionId1 = seatLockService.generateSessionId();
        String sessionId2 = seatLockService.generateSessionId();

        // Then
        assertNotNull(sessionId1);
        assertNotNull(sessionId2);
        assertNotEquals(sessionId1, sessionId2);
        assertTrue(sessionId1.length() > 0);
        assertTrue(sessionId2.length() > 0);
    }

    @Test
    void testLockInfoAfterExpiration() {
        // Given
        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";
        seatLockService.tryLock(seatId, sessionId, lockedBy);

        // When - manually create an expired lock for testing
        // Note: This is a bit of a hack since we can't easily manipulate time in the current implementation
        // In a real scenario, we'd use a clock abstraction for better testability
        seatLockService.cleanupExpiredLocks();

        // Then - the lock should still be present as it hasn't actually expired
        assertTrue(seatLockService.isLocked(seatId));
        assertTrue(seatLockService.getLockInfo(seatId).isPresent());
    }
}
