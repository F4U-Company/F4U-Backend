package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;

import java.util.List;

public interface ReservationService {
    Reservation createReservation(ReservationRequest req);
    List<Reservation> getAll();
    List<Reservation> getByFlight(Long flightId);
}
