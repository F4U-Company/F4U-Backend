package com.fly.company.f4u_backend.controller;

import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.repository.FlightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightController {

    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> all() {
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
