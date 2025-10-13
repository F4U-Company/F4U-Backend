package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationRequest {
    @JsonProperty("flightId")
    private Long flightId;
    
    @JsonProperty("seatId")
    private Long seatId;
    
    @JsonProperty("passengerName")
    private String passengerName;
    
    @JsonProperty("passengerEmail")
    private String passengerEmail;
    
    @JsonProperty("sessionId")
    private String sessionId; // Required for seat lock validation

    public ReservationRequest() {}

    public Long getFlightId() { 
        return flightId; 
    }
    
    public void setFlightId(Long flightId) { 
        this.flightId = flightId; 
    }
    
    public Long getSeatId() { 
        return seatId; 
    }
    
    public void setSeatId(Long seatId) { 
        this.seatId = seatId; 
    }
    
    public String getPassengerName() { 
        return passengerName; 
    }
    
    public void setPassengerName(String passengerName) { 
        this.passengerName = passengerName; 
    }
    
    public String getPassengerEmail() { 
        return passengerEmail; 
    }
    
    public void setPassengerEmail(String passengerEmail) { 
        this.passengerEmail = passengerEmail; 
    }
    
    public String getSessionId() { 
        return sessionId; 
    }
    
    public void setSessionId(String sessionId) { 
        this.sessionId = sessionId; 
    }

    @Override
    public String toString() {
        return "ReservationRequest{" +
                "flightId=" + flightId +
                ", seatId=" + seatId +
                ", passengerName='" + passengerName + '\'' +
                ", passengerEmail='" + passengerEmail + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
