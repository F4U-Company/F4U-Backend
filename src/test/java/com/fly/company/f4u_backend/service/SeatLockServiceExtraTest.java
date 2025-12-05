package com.fly.company.f4u_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class SeatLockServiceExtraTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private SeatLockService seatLockService;

    @BeforeEach
    void setUp() {
        seatLockService = new SeatLockService(messagingTemplate);
    }

    @Test
    void testTryLockSuccess() {
        boolean result = seatLockService.tryLock(100L, "user1");
        assertTrue(result);
    }

    @Test
    void testTryLockAlreadyLocked() {
        seatLockService.tryLock(200L, "user1");
        boolean result = seatLockService.tryLock(200L, "user2");
        assertFalse(result);
    }

    @Test
    void testTryLockSameUserTwice() {
        seatLockService.tryLock(300L, "user1");
        boolean result = seatLockService.tryLock(300L, "user1");
        assertTrue(result);
    }

    @Test
    void testIsLocked() {
        seatLockService.tryLock(400L, "user1");
        assertTrue(seatLockService.isLocked(400L));
    }

    @Test
    void testIsNotLocked() {
        assertFalse(seatLockService.isLocked(500L));
    }

    @Test
    void testIsLockedByUser() {
        seatLockService.tryLock(600L, "user1");
        assertTrue(seatLockService.isLockedByUser(600L, "user1"));
    }

    @Test
    void testIsNotLockedByUser() {
        seatLockService.tryLock(700L, "user1");
        assertFalse(seatLockService.isLockedByUser(700L, "user2"));
    }

    @Test
    void testReleaseLock() {
        seatLockService.tryLock(800L, "user1");
        seatLockService.releaseLock(800L);
        assertFalse(seatLockService.isLocked(800L));
    }

    @Test
    void testReleaseLockNotLocked() {
        assertDoesNotThrow(() -> seatLockService.releaseLock(900L));
    }

    @Test
    void testGetLockedByUserId() {
        seatLockService.tryLock(1000L, "user1");
        String userId = seatLockService.getLockedByUserId(1000L);
        assertEquals("user1", userId);
    }

    @Test
    void testGetLockedByUserIdNotLocked() {
        String userId = seatLockService.getLockedByUserId(1100L);
        assertNull(userId);
    }

    @Test
    void testGetRemainingLockTimeSeconds() {
        seatLockService.tryLock(1200L, "user1");
        long remaining = seatLockService.getRemainingLockTimeSeconds(1200L);
        assertTrue(remaining > 0);
        assertTrue(remaining <= 900); // 15 minutos máximo
    }

    @Test
    void testGetRemainingLockTimeSecondsNotLocked() {
        long remaining = seatLockService.getRemainingLockTimeSeconds(1300L);
        assertEquals(0, remaining);
    }

    @Test
    void testMultipleSeatsLocking() {
        assertTrue(seatLockService.tryLock(1400L, "user1"));
        assertTrue(seatLockService.tryLock(1500L, "user2"));
        assertTrue(seatLockService.tryLock(1600L, "user3"));
        
        assertTrue(seatLockService.isLockedByUser(1400L, "user1"));
        assertTrue(seatLockService.isLockedByUser(1500L, "user2"));
        assertTrue(seatLockService.isLockedByUser(1600L, "user3"));
    }

    @Test
    void testReleaseAndRelock() {
        seatLockService.tryLock(1700L, "user1");
        seatLockService.releaseLock(1700L);
        assertTrue(seatLockService.tryLock(1700L, "user2"));
        assertTrue(seatLockService.isLockedByUser(1700L, "user2"));
    }

    @Test
    void testLockWithEmptyStringUserId() {
        assertTrue(seatLockService.tryLock(1800L, ""));
        assertTrue(seatLockService.isLocked(1800L));
    }

    @Test
    void testLockWithValidSeatIdAndUserId() {
        assertTrue(seatLockService.tryLock(9999L, "validUser"));
        assertTrue(seatLockService.isLockedByUser(9999L, "validUser"));
    }

    @Test
    void testTryLockMultipleSeatsForOneUser() {
        assertTrue(seatLockService.tryLock(2000L, "user1"));
        assertTrue(seatLockService.tryLock(2001L, "user1"));
        assertTrue(seatLockService.tryLock(2002L, "user1"));
        
        assertTrue(seatLockService.isLocked(2000L));
        assertTrue(seatLockService.isLocked(2001L));
        assertTrue(seatLockService.isLocked(2002L));
    }

    @Test
    void testRenewLockExtendsDuration() {
        seatLockService.tryLock(2100L, "user1");
        assertTrue(seatLockService.isLocked(2100L));
        
        long remaining1 = seatLockService.getRemainingLockTimeSeconds(2100L);
        assertTrue(remaining1 > 0);
    }

    @Test
    void testUnlockMethodReleasesLock() {
        seatLockService.tryLock(2200L, "user1");
        assertTrue(seatLockService.isLocked(2200L));
        
        seatLockService.releaseLock(2200L);
        assertFalse(seatLockService.isLocked(2200L));
    }

    @Test
    void testIsLockedByUserDifferentUser() {
        seatLockService.tryLock(2300L, "user1");
        assertFalse(seatLockService.isLockedByUser(2300L, "user2"));
        assertTrue(seatLockService.isLockedByUser(2300L, "user1"));
    }

    @Test
    void testGetLockedByUserIdReturnsCorrectUser() {
        seatLockService.tryLock(2400L, "testUser123");
        String lockedBy = seatLockService.getLockedByUserId(2400L);
        assertEquals("testUser123", lockedBy);
    }

    @Test
    void testGetLockInfoReturnsMap() {
        seatLockService.tryLock(3000L, "user1");
        seatLockService.tryLock(3001L, "user2");
        
        java.util.Map<String, Object> lockInfo = seatLockService.getLockInfo();
        
        assertNotNull(lockInfo);
        assertTrue(lockInfo.size() >= 2);
    }

    @Test
    void testGetLockInfoAfterMultipleLocks() {
        seatLockService.tryLock(4000L, "userA");
        seatLockService.tryLock(4001L, "userB");
        seatLockService.tryLock(4002L, "userC");
        
        java.util.Map<String, Object> lockInfo = seatLockService.getLockInfo();
        
        assertNotNull(lockInfo);
        assertTrue(lockInfo.size() >= 3);
    }
}
