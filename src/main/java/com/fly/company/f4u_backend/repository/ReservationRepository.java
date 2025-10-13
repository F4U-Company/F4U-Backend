package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByFlightId(Long flightId);
    List<Reservation> findBySeatId(Long seatId);
}
