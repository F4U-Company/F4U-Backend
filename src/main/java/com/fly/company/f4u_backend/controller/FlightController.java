package com.fly.company.f4u_backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.repository.FlightRepository;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final Logger log = LoggerFactory.getLogger(FlightController.class);
    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Buscar vuelos por origen, destino y fecha
     * GET /api/flights/search?origin=1&destination=2&date=2025-10-25
     */
    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam("origin") Long originId,
            @RequestParam("destination") Long destinationId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /api/flights/search - Buscando vuelos: origen={}, destino={}, fecha={}", 
                 originId, destinationId, date);
        
        try {
            // Convertir LocalDate a LocalDateTime al inicio del día
            LocalDateTime dateTime = date.atStartOfDay();
            
            List<Flight> flights = flightRepository.findFlightsByOriginDestinationAndDate(
                originId, destinationId, dateTime
            );
            
            log.info("Se encontraron {} vuelos para la búsqueda", flights.size());
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            log.error("Error al buscar vuelos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Flight>> all() {
        log.info("GET /api/flights - Obteniendo todos los vuelos");
        return ResponseEntity.ok(flightRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Flight> create(@RequestBody Flight f) {
        Flight saved = flightRepository.save(f);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return flightRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!flightRepository.existsById(id)) return ResponseEntity.notFound().build();
        flightRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
