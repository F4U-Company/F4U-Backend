package com.fly.company.f4u_backend.service;

import java.util.List;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;

public interface ReservationService {
    Reservation createReservation(ReservationRequest req);
    List<Reservation> getAll();
    List<Reservation> getByFlight(Long vueloId);
}
