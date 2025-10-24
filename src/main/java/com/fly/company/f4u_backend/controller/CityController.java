package com.fly.company.f4u_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.repository.CityRepository;

/**
 * Controlador REST para gestionar ciudades
 */
@RestController
@RequestMapping("/api/cities")
public class CityController {
    
    private static final Logger log = LoggerFactory.getLogger(CityController.class);
    
    @Autowired
    private CityRepository cityRepository;
    
    /**
     * Obtener todas las ciudades activas
     */
    @GetMapping
    public ResponseEntity<List<City>> getAllActiveCities() {
        log.info("GET /api/cities - Obteniendo todas las ciudades activas");
        try {
            List<City> cities = cityRepository.findByActivoTrue();
            log.info("Se encontraron {} ciudades activas", cities.size());
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            log.error("Error al obtener ciudades: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtener ciudad por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        log.info("GET /api/cities/{} - Buscando ciudad por ID", id);
        return cityRepository.findById(id)
                .map(city -> {
                    log.info("Ciudad encontrada: {} ({})", city.getNombre(), city.getCodigoIata());
                    return ResponseEntity.ok(city);
                })
                .orElseGet(() -> {
                    log.warn("Ciudad con ID {} no encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    /**
     * Obtener ciudad por código IATA
     */
    @GetMapping("/iata/{code}")
    public ResponseEntity<City> getCityByIataCode(@PathVariable String code) {
        log.info("GET /api/cities/iata/{} - Buscando ciudad por código IATA", code);
        return cityRepository.findByCodigoIata(code.toUpperCase())
                .map(city -> {
                    log.info("Ciudad encontrada: {} ({})", city.getNombre(), city.getCodigoIata());
                    return ResponseEntity.ok(city);
                })
                .orElseGet(() -> {
                    log.warn("Ciudad con código IATA {} no encontrada", code);
                    return ResponseEntity.notFound().build();
                });
    }
    
    /**
     * Obtener ciudades por país
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<City>> getCitiesByCountry(@PathVariable String country) {
        log.info("GET /api/cities/country/{} - Buscando ciudades por país", country);
        try {
            List<City> cities = cityRepository.findByPaisAndActivoTrue(country);
            log.info("Se encontraron {} ciudades en {}", cities.size(), country);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            log.error("Error al obtener ciudades por país: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
