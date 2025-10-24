package com.fly.company.f4u_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fly.company.f4u_backend.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVueloId(Long vueloId);
}
