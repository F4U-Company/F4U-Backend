package com.fly.company.f4u_backend.repository;

import com.fly.company.f4u_backend.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
  
}
