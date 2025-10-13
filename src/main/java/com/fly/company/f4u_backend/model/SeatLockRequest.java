package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for seat locking operations
 */
public class SeatLockRequest {
    
    @JsonProperty("seatId")
    private Long seatId;
    
    @JsonProperty("sessionId")
    private String sessionId;
    
    @JsonProperty("lockedBy")
    private String lockedBy; // IP address or user identifier

    public SeatLockRequest() {}

    public SeatLockRequest(Long seatId, String sessionId, String lockedBy) {
        this.seatId = seatId;
        this.sessionId = sessionId;
        this.lockedBy = lockedBy;
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

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    @Override
    public String toString() {
        return "SeatLockRequest{" +
                "seatId=" + seatId +
                ", sessionId='" + sessionId + '\'' +
                ", lockedBy='" + lockedBy + '\'' +
                '}';
    }
}
