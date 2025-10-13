package com.fly.company.f4u_backend.model;

import jakarta.persistence.*;

@Entity
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber; // ej. "12A"
    private boolean paid;      // si el usuario pagó por la selección de asiento
    private boolean assigned;  // si ya está asignado a una reserva
    private Long flightId;     // relación simple: id del vuelo

    public Seat() {}

    public Seat(String seatNumber, boolean paid, Long flightId) {
        this.seatNumber = seatNumber;
        this.paid = paid;
        this.assigned = false;
        this.flightId = flightId;
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public boolean isAssigned() { return assigned; }
    public void setAssigned(boolean assigned) { this.assigned = assigned; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
}
