package com.fly.company.f4u_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.repository.FlightRepository;

@RestController
@RequestMapping("/api/flights")
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
