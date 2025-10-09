package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightId(Long flightId);
}
