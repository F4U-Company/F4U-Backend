package com.fly.company.f4u_backend.model;

public class ReservationRequest {
    private Long flightId;
    private Long seatId;
    private String passengerName;
    private String passengerEmail;

    public ReservationRequest() {}

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
}
