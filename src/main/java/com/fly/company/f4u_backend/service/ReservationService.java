package com.fly.company.f4u_backend.service;

import java.util.List;
import java.util.Map;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;

public interface ReservationService {
    Reservation createReservation(ReservationRequest req);
    List<Reservation> getAll();
    List<Reservation> getByFlight(Long vueloId);
    
    // Métodos existentes
    List<Reservation> getReservationsByUserEmail(String email);
    List<Reservation> getActiveReservationsByUserEmail(String email);
    Map<String, Object> getUserStats(String email);
    Reservation getReservationByCode(String codigo);
    
    // NUEVOS MÉTODOS CON INFORMACIÓN BÁSICA (sin relaciones JPA)
    List<Reservation> getReservationsWithBasicInfoByEmail(String email);
    List<Reservation> getActiveReservationsWithBasicInfoByEmail(String email);
}