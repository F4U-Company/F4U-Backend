package com.fly.company.f4u_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Flight {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String flightNumber;
    private String origin;
    private String destination;

    // Aseguramos que Jackson acepte el formato "yyyy-MM-dd'T'HH:mm:ss"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    private String gate;
    private String status; // ON_TIME, DELAYED, CANCELLED, etc.

    public Flight() {}

    public Flight(String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, String gate, String status) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.gate = gate;
        this.status = status;
    }

    // getters y setters
    public Long getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
