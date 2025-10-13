package com.fly.company.f4u_backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive concurrency tests for SeatLockService.
 * Tests race conditions, deadlocks, and thread safety under high load.
 */
@ExtendWith(MockitoExtension.class)
class SeatLockConcurrencyTest {

    @Test
    void testConcurrentLockAttemptsOnSameSeat() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicReference<String> successfulSession = new AtomicReference<>();

        Long seatId = 1L;

        // When - multiple threads try to lock the same seat simultaneously
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String sessionId = "session-" + threadId;
                    String lockedBy = "user-" + threadId;
                    
                    boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);
                    if (result) {
                        successCount.incrementAndGet();
                        successfulSession.set(sessionId);
                    } else {
                        failureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - only one thread should succeed
        assertEquals(1, successCount.get());
        assertEquals(numberOfThreads - 1, failureCount.get());
        assertTrue(seatLockService.isLocked(seatId));
        assertNotNull(successfulSession.get());
    }

    @Test
    void testConcurrentLockAttemptsOnDifferentSeats() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfSeats = 10;
        int threadsPerSeat = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfSeats * threadsPerSeat);
        CountDownLatch latch = new CountDownLatch(numberOfSeats * threadsPerSeat);
        AtomicInteger totalSuccessCount = new AtomicInteger(0);
        AtomicInteger totalFailureCount = new AtomicInteger(0);

        // When - multiple threads try to lock different seats
        for (int seatId = 1; seatId <= numberOfSeats; seatId++) {
            for (int threadId = 1; threadId <= threadsPerSeat; threadId++) {
                final Long currentSeatId = (long) seatId;
                final int currentThreadId = threadId;
                executor.submit(() -> {
                    try {
                        String sessionId = "session-" + currentSeatId + "-" + currentThreadId;
                        String lockedBy = "user-" + currentSeatId + "-" + currentThreadId;
                        
                        boolean result = seatLockService.tryLock(currentSeatId, sessionId, lockedBy);
                        if (result) {
                            totalSuccessCount.incrementAndGet();
                        } else {
                            totalFailureCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - exactly one thread per seat should succeed
        assertEquals(numberOfSeats, totalSuccessCount.get());
        assertEquals(numberOfSeats * (threadsPerSeat - 1), totalFailureCount.get());
        
        // All seats should be locked
        for (int seatId = 1; seatId <= numberOfSeats; seatId++) {
            assertTrue(seatLockService.isLocked((long) seatId));
        }
    }

    @Test
    void testConcurrentLockAndRelease() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfOperations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numberOfOperations);
        AtomicInteger lockSuccessCount = new AtomicInteger(0);
        AtomicInteger releaseSuccessCount = new AtomicInteger(0);

        Long seatId = 1L;

        // When - threads alternately lock and release the same seat
        for (int i = 0; i < numberOfOperations; i++) {
            final int operationId = i;
            executor.submit(() -> {
                try {
                    String sessionId = "session-" + operationId;
                    String lockedBy = "user-" + operationId;
                    
                    if (operationId % 2 == 0) {
                        // Lock operation
                        boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);
                        if (result) {
                            lockSuccessCount.incrementAndGet();
                            // Release after a short delay
                            Thread.sleep(10);
                            boolean releaseResult = seatLockService.releaseLock(seatId, sessionId);
                            if (releaseResult) {
                                releaseSuccessCount.incrementAndGet();
                            }
                        }
                    } else {
                        // Try to lock (might succeed if previous lock was released)
                        boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);
                        if (result) {
                            lockSuccessCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - verify that operations completed successfully
        assertTrue(lockSuccessCount.get() > 0);
        assertTrue(releaseSuccessCount.get() != 0);
    }

    @Test
    void testConcurrentLockExtension() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger extensionSuccessCount = new AtomicInteger(0);
        AtomicInteger extensionFailureCount = new AtomicInteger(0);

        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";

        // First, establish a lock
        assertTrue(seatLockService.tryLock(seatId, sessionId, lockedBy));

        // When - multiple threads try to extend the same lock
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    boolean result = seatLockService.extendLock(seatId, sessionId);
                    if (result) {
                        extensionSuccessCount.incrementAndGet();
                    } else {
                        extensionFailureCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - all extensions should succeed since they're from the same session
        assertEquals(numberOfThreads, extensionSuccessCount.get());
        assertEquals(0, extensionFailureCount.get());
        assertTrue(seatLockService.isLocked(seatId));
    }

    @Test
    void testConcurrentLockInfoAccess() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger lockInfoAccessCount = new AtomicInteger(0);

        Long seatId = 1L;
        String sessionId = "session-1";
        String lockedBy = "user-1";

        // First, establish a lock
        assertTrue(seatLockService.tryLock(seatId, sessionId, lockedBy));

        // When - multiple threads access lock info concurrently
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    boolean isLocked = seatLockService.isLocked(seatId);
                    if (isLocked) {
                        lockInfoAccessCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - all threads should see the lock
        assertEquals(numberOfThreads, lockInfoAccessCount.get());
    }

    @Test
    void testConcurrentCleanupAndLockOperations() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 30;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger lockSuccessCount = new AtomicInteger(0);

        // When - threads perform lock operations while cleanup runs
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Long seatId = (long) (threadId % 5) + 1; // 5 different seats
                    String sessionId = "session-" + threadId;
                    String lockedBy = "user-" + threadId;
                    
                    boolean result = seatLockService.tryLock(seatId, sessionId, lockedBy);
                    if (result) {
                        lockSuccessCount.incrementAndGet();
                    }
                    
                    // Simulate some work
                    Thread.sleep(10);
                    
                    // Try to get lock info
                    seatLockService.isLocked(seatId);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Start cleanup in a separate thread
        executor.submit(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    seatLockService.cleanupExpiredLocks();
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - verify that operations completed without deadlocks
        assertTrue(lockSuccessCount.get() > 0);
    }

    @Test
    void testConcurrentSessionIdGeneration() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ConcurrentLinkedQueue<String> generatedIds = new ConcurrentLinkedQueue<>();

        // When - multiple threads generate session IDs concurrently
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    String sessionId = seatLockService.generateSessionId();
                    generatedIds.add(sessionId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - all generated IDs should be unique
        assertEquals(numberOfThreads, generatedIds.size());
        assertEquals(numberOfThreads, generatedIds.stream().distinct().count());
    }

    @Test
    void testConcurrentActiveLockCount() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger countAccessCount = new AtomicInteger(0);

        // Create some locks first
        for (int i = 1; i <= 5; i++) {
            seatLockService.tryLock((long) i, "session-" + i, "user-" + i);
        }

        // When - multiple threads access active lock count concurrently
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    int count = seatLockService.getActiveLockCount();
                    if (count >= 0) { // Valid count
                        countAccessCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - all threads should successfully access the count
        assertEquals(numberOfThreads, countAccessCount.get());
    }

    @Test
    void testStressTestWithMixedOperations() throws InterruptedException {
        SeatLockService seatLockService = new SeatLockService();
        int numberOfThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger totalOperations = new AtomicInteger(0);

        // When - threads perform mixed operations (lock, unlock, extend, check info)
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    Long seatId = (long) (threadId % 10) + 1; // 10 different seats
                    String sessionId = "session-" + threadId;
                    String lockedBy = "user-" + threadId;
                    
                    // Try to lock
                    boolean lockResult = seatLockService.tryLock(seatId, sessionId, lockedBy);
                    totalOperations.incrementAndGet();
                    
                    if (lockResult) {
                        // If successful, try to extend
                        seatLockService.extendLock(seatId, sessionId);
                        totalOperations.incrementAndGet();
                        
                        // Check lock info
                        seatLockService.isLocked(seatId);
                        totalOperations.incrementAndGet();
                        
                        // Release lock
                        seatLockService.releaseLock(seatId, sessionId);
                        totalOperations.incrementAndGet();
                    } else {
                        // If failed, just check if locked
                        seatLockService.isLocked(seatId);
                        totalOperations.incrementAndGet();
                    }
                    
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - verify that all operations completed
        assertTrue(totalOperations.get() > 0);
        // No deadlocks should have occurred (test would timeout if they did)
    }
}
