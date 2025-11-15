package com.fly.company.f4u_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fly.company.f4u_backend.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVueloId(Long vueloId);
}
