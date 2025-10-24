package com.fly.company.f4u_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fly.company.f4u_backend.model.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    /**
     * Buscar vuelos activos entre dos ciudades en una fecha específica
     */
    @Query("SELECT f FROM Flight f WHERE f.ciudadOrigen.id = :origenId " +
           "AND f.ciudadDestino.id = :destinoId " +
           "AND CAST(f.fechaSalida AS date) = CAST(:fecha AS date) " +
           "AND f.activo = true " +
           "ORDER BY f.fechaSalida")
    List<Flight> findFlightsByOriginDestinationAndDate(
        @Param("origenId") Long origenId, 
        @Param("destinoId") Long destinoId, 
        @Param("fecha") LocalDateTime fecha
    );
}
