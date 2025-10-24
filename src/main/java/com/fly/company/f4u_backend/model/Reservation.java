package com.fly.company.f4u_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long flightId;
    private Long seatId;
    private String passengerName;
    private String passengerEmail;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private LocalDateTime createdAt;

    public Reservation() {}

    public Reservation(Long flightId, Long seatId, String passengerName, String passengerEmail, String status) {
        this.flightId = flightId;
        this.seatId = seatId;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // getters y setters
    public Long getId() { return id; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
