package com.fly.company.f4u_backend.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class FlightTest {

    @Test
    void testFlightCreation() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setNumeroVuelo("AV123");
        
        assertNotNull(flight);
        assertEquals(1L, flight.getId());
        assertEquals("AV123", flight.getNumeroVuelo());
    }

    @Test
    void testFlightDates() {
        Flight flight = new Flight();
        LocalDateTime salida = LocalDateTime.now().plusDays(7);
        LocalDateTime llegada = salida.plusHours(2);
        
        flight.setFechaSalida(salida);
        flight.setFechaLlegada(llegada);
        
        assertEquals(salida, flight.getFechaSalida());
        assertEquals(llegada, flight.getFechaLlegada());
    }

    @Test
    void testFlightCities() {
        Flight flight = new Flight();
        City origen = new City();
        origen.setId(1L);
        origen.setNombre("Bogotá");
        
        City destino = new City();
        destino.setId(2L);
        destino.setNombre("Medellín");
        
        flight.setCiudadOrigen(origen);
        flight.setCiudadDestino(destino);
        
        assertEquals(origen, flight.getCiudadOrigen());
        assertEquals(destino, flight.getCiudadDestino());
    }

    @Test
    void testFlightAllFields() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setNumeroVuelo("AV789");
        flight.setFechaSalida(LocalDateTime.now());
        flight.setFechaLlegada(LocalDateTime.now().plusHours(3));
        
        assertNotNull(flight.getId());
        assertNotNull(flight.getNumeroVuelo());
        assertNotNull(flight.getFechaSalida());
        assertNotNull(flight.getFechaLlegada());
    }

    @Test
    void testFlightEquality() {
        Flight flight1 = new Flight();
        flight1.setId(1L);
        flight1.setNumeroVuelo("AV100");
        
        Flight flight2 = new Flight();
        flight2.setId(1L);
        flight2.setNumeroVuelo("AV100");
        
        assertEquals(flight1.getId(), flight2.getId());
        assertEquals(flight1.getNumeroVuelo(), flight2.getNumeroVuelo());
    }
}
