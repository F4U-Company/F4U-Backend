package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Response DTO for seat locking operations
 */
public class SeatLockResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("seatId")
    private Long seatId;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("lockedAt")
    private LocalDateTime lockedAt;
    
    @JsonProperty("expiresAt")
    private LocalDateTime expiresAt;
    
    @JsonProperty("remainingSeconds")
    private long remainingSeconds;

    public SeatLockResponse() {}

    public SeatLockResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public SeatLockResponse(boolean success, String message, SeatLock lock) {
        this.success = success;
        this.message = message;
        if (lock != null) {
            this.seatId = lock.getSeatId();
            this.sessionId = lock.getSessionId();
            this.lockedAt = lock.getLockedAt();
            this.expiresAt = lock.getExpiresAt();
            this.remainingSeconds = lock.getRemainingSeconds();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public void setRemainingSeconds(long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    @Override
    public String toString() {
        return "SeatLockResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", seatId=" + seatId +
                ", sessionId='" + sessionId + '\'' +
                ", lockedAt=" + lockedAt +
                ", expiresAt=" + expiresAt +
                ", remainingSeconds=" + remainingSeconds +
                '}';
    }
}
