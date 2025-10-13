package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.SeatLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for managing seat locks with thread-safe operations and proper concurrency control.
 * Implements 5-minute temporary locks to prevent double assignment during booking process.
 */
@Service
public class SeatLockService {
    
    private static final Logger logger = LoggerFactory.getLogger(SeatLockService.class);
    
    // 5 minutes lock duration
    private static final int LOCK_DURATION_MINUTES = 5;
    
    // Thread-safe storage for seat locks
    private final Map<Long, SeatLock> locks = new ConcurrentHashMap<>();
    
    // Read-write lock for additional synchronization on critical operations
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

    /**
     * Attempts to lock a seat for the specified session.
     * 
     * @param seatId The ID of the seat to lock
     * @param sessionId The session ID requesting the lock
     * @param lockedBy Identifier of who is locking (IP, user ID, etc.)
     * @return true if the lock was successfully acquired, false if already locked
     */
    public boolean tryLock(Long seatId, String sessionId, String lockedBy) {
        if (seatId == null || sessionId == null || lockedBy == null) {
            logger.warn("Invalid parameters for seat lock: seatId={}, sessionId={}, lockedBy={}", 
                       seatId, sessionId, lockedBy);
            return false;
        }

        writeLock.lock();
        try {
            SeatLock existingLock = locks.get(seatId);
            
            // Check if seat is already locked and not expired
            if (existingLock != null && !existingLock.isExpired()) {
                logger.info("Seat {} is already locked by session {} until {}", 
                           seatId, existingLock.getSessionId(), existingLock.getExpiresAt());
                return false;
            }
            
            // Create new lock
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
            SeatLock newLock = new SeatLock(seatId, sessionId, now, expiresAt, lockedBy);
            
            locks.put(seatId, newLock);
            
            logger.info("Successfully locked seat {} for session {} until {} by {}", 
                       seatId, sessionId, expiresAt, lockedBy);
            
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Releases a lock for a specific seat and session.
     * 
     * @param seatId The ID of the seat to unlock
     * @param sessionId The session ID that owns the lock
     * @return true if the lock was successfully released, false if not found or not owned by session
     */
    public boolean releaseLock(Long seatId, String sessionId) {
        if (seatId == null || sessionId == null) {
            logger.warn("Invalid parameters for seat unlock: seatId={}, sessionId={}", seatId, sessionId);
            return false;
        }

        writeLock.lock();
        try {
            SeatLock existingLock = locks.get(seatId);
            
            if (existingLock == null) {
                logger.debug("No lock found for seat {}", seatId);
                return false;
            }
            
            if (!sessionId.equals(existingLock.getSessionId())) {
                logger.warn("Session {} attempted to release lock owned by session {} for seat {}", 
                           sessionId, existingLock.getSessionId(), seatId);
                return false;
            }
            
            locks.remove(seatId);
            logger.info("Successfully released lock for seat {} by session {}", seatId, sessionId);
            
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Checks if a seat is currently locked.
     * 
     * @param seatId The ID of the seat to check
     * @return true if the seat is locked and not expired, false otherwise
     */
    public boolean isLocked(Long seatId) {
        if (seatId == null) {
            return false;
        }

        readLock.lock();
        try {
            SeatLock lock = locks.get(seatId);
            boolean locked = lock != null && !lock.isExpired();
            
            if (locked) {
                logger.debug("Seat {} is locked by session {} until {}", 
                           seatId, lock.getSessionId(), lock.getExpiresAt());
            }
            
            return locked;
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Gets the current lock information for a seat.
     * 
     * @param seatId The ID of the seat
     * @return Optional containing the lock information if locked and not expired
     */
    public Optional<SeatLock> getLockInfo(Long seatId) {
        if (seatId == null) {
            return Optional.empty();
        }

        readLock.lock();
        try {
            SeatLock lock = locks.get(seatId);
            if (lock != null && !lock.isExpired()) {
                return Optional.of(lock);
            }
            return Optional.empty();
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Extends the lock duration for a seat if it's owned by the specified session.
     * 
     * @param seatId The ID of the seat
     * @param sessionId The session ID that owns the lock
     * @return true if the lock was successfully extended, false otherwise
     */
    public boolean extendLock(Long seatId, String sessionId) {
        if (seatId == null || sessionId == null) {
            logger.warn("Invalid parameters for lock extension: seatId={}, sessionId={}", seatId, sessionId);
            return false;
        }

        writeLock.lock();
        try {
            SeatLock existingLock = locks.get(seatId);
            
            if (existingLock == null || existingLock.isExpired()) {
                logger.debug("No valid lock found for seat {} to extend", seatId);
                return false;
            }
            
            if (!sessionId.equals(existingLock.getSessionId())) {
                logger.warn("Session {} attempted to extend lock owned by session {} for seat {}", 
                           sessionId, existingLock.getSessionId(), seatId);
                return false;
            }
            
            // Create new lock with extended expiration
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime newExpiresAt = now.plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
            SeatLock extendedLock = new SeatLock(seatId, sessionId, existingLock.getLockedAt(), 
                                               newExpiresAt, existingLock.getLockedBy());
            
            locks.put(seatId, extendedLock);
            
            logger.info("Successfully extended lock for seat {} by session {} until {}", 
                       seatId, sessionId, newExpiresAt);
            
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Gets the number of currently active locks.
     * 
     * @return The count of active (non-expired) locks
     */
    public int getActiveLockCount() {
        readLock.lock();
        try {
            return (int) locks.values().stream()
                    .filter(lock -> !lock.isExpired())
                    .count();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Scheduled task to clean up expired locks every minute.
     * This prevents memory leaks from expired locks that weren't properly released.
     */
    @Scheduled(fixedRate = 60_000) // Run every minute
    public void cleanupExpiredLocks() {
        writeLock.lock();
        try {
            int beforeCount = locks.size();
            locks.entrySet().removeIf(entry -> entry.getValue().isExpired());
            int afterCount = locks.size();
            int removedCount = beforeCount - afterCount;
            
            if (removedCount > 0) {
                logger.info("Cleaned up {} expired locks. Active locks: {}", removedCount, afterCount);
            }
            
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Generates a unique session ID for seat locking.
     * 
     * @return A unique session identifier
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
