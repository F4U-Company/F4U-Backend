package com.fly.company.f4u_backend.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a seat lock with metadata for tracking and debugging
 */
public class SeatLock {
    private final Long seatId;
    private final String sessionId;
    private final LocalDateTime lockedAt;
    private final LocalDateTime expiresAt;
    private final String lockedBy; // IP address or user identifier

    public SeatLock(Long seatId, String sessionId, LocalDateTime lockedAt, 
                   LocalDateTime expiresAt, String lockedBy) {
        this.seatId = seatId;
        this.sessionId = sessionId;
        this.lockedAt = lockedAt;
        this.expiresAt = expiresAt;
        this.lockedBy = lockedBy;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public long getRemainingSeconds() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatLock seatLock = (SeatLock) o;
        return Objects.equals(seatId, seatLock.seatId) &&
               Objects.equals(sessionId, seatLock.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, sessionId);
    }

    @Override
    public String toString() {
        return "SeatLock{" +
                "seatId=" + seatId +
                ", sessionId='" + sessionId + '\'' +
                ", lockedAt=" + lockedAt +
                ", expiresAt=" + expiresAt +
                ", lockedBy='" + lockedBy + '\'' +
                ", remainingSeconds=" + getRemainingSeconds() +
                '}';
    }
}
