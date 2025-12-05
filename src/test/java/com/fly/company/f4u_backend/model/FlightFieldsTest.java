package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FlightFieldsTest {

    @Test
    void testFlightDefaultConstructor() {
        Flight flight = new Flight();
        assertNotNull(flight);
    }

    @Test
    void testGetSetAerolineaId() {
        Flight flight = new Flight();
        flight.setAerolineaId(100L);
        assertEquals(100L, flight.getAerolineaId());
    }

    @Test
    void testGetSetAvionId() {
        Flight flight = new Flight();
        flight.setAvionId(200L);
        assertEquals(200L, flight.getAvionId());
    }

    @Test
    void testGetSetCiudadOrigen() {
        Flight flight = new Flight();
        City origin = new City();
        origin.setId(1L);
        flight.setCiudadOrigen(origin);
        assertEquals(origin, flight.getCiudadOrigen());
        assertEquals(1L, flight.getCiudadOrigen().getId());
    }

    @Test
    void testGetSetCiudadDestino() {
        Flight flight = new Flight();
        City dest = new City();
        dest.setId(2L);
        flight.setCiudadDestino(dest);
        assertEquals(dest, flight.getCiudadDestino());
        assertEquals(2L, flight.getCiudadDestino().getId());
    }

    @Test
    void testGetSetDuracionMinutos() {
        Flight flight = new Flight();
        flight.setDuracionMinutos(180);
        assertEquals(180, flight.getDuracionMinutos());
    }

    @Test
    void testGetSetPrecioEconomica() {
        Flight flight = new Flight();
        BigDecimal precio = new BigDecimal("250.50");
        flight.setPrecioEconomica(precio);
        assertEquals(precio, flight.getPrecioEconomica());
    }

    @Test
    void testGetSetPrecioEjecutiva() {
        Flight flight = new Flight();
        BigDecimal precio = new BigDecimal("500.00");
        flight.setPrecioEjecutiva(precio);
        assertEquals(precio, flight.getPrecioEjecutiva());
    }

    @Test
    void testGetSetPrecioPrimeraClase() {
        Flight flight = new Flight();
        BigDecimal precio = new BigDecimal("1000.00");
        flight.setPrecioPrimeraClase(precio);
        assertEquals(precio, flight.getPrecioPrimeraClase());
    }

    @Test
    void testGetSetAsientosDisponiblesEconomica() {
        Flight flight = new Flight();
        flight.setAsientosDisponiblesEconomica(100);
        assertEquals(100, flight.getAsientosDisponiblesEconomica());
    }

    @Test
    void testGetSetAsientosDisponiblesEjecutiva() {
        Flight flight = new Flight();
        flight.setAsientosDisponiblesEjecutiva(20);
        assertEquals(20, flight.getAsientosDisponiblesEjecutiva());
    }

    @Test
    void testGetSetAsientosDisponiblesPrimeraClase() {
        Flight flight = new Flight();
        flight.setAsientosDisponiblesPrimeraClase(10);
        assertEquals(10, flight.getAsientosDisponiblesPrimeraClase());
    }

    @Test
    void testGetSetEstado() {
        Flight flight = new Flight();
        flight.setEstado("PROGRAMADO");
        assertEquals("PROGRAMADO", flight.getEstado());
    }

    @Test
    void testGetSetPuertaEmbarque() {
        Flight flight = new Flight();
        flight.setPuertaEmbarque("A12");
        assertEquals("A12", flight.getPuertaEmbarque());
    }

    @Test
    void testGetSetTerminal() {
        Flight flight = new Flight();
        flight.setTerminal("T1");
        assertEquals("T1", flight.getTerminal());
    }

    @Test
    void testGetSetActivo() {
        Flight flight = new Flight();
        flight.setActivo(true);
        assertTrue(flight.getActivo());
        flight.setActivo(false);
        assertFalse(flight.getActivo());
    }

    @Test
    void testGetSetFechaCreacion() {
        Flight flight = new Flight();
        LocalDateTime now = LocalDateTime.now();
        flight.setFechaCreacion(now);
        assertEquals(now, flight.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        Flight flight = new Flight();
        LocalDateTime now = LocalDateTime.now();
        flight.setFechaActualizacion(now);
        assertEquals(now, flight.getFechaActualizacion());
    }

    @Test
    void testMultipleCitiesAssignment() {
        Flight flight = new Flight();
        City origin = new City();
        origin.setId(10L);
        City dest = new City();
        dest.setId(20L);
        
        flight.setCiudadOrigen(origin);
        flight.setCiudadDestino(dest);
        
        assertEquals(10L, flight.getCiudadOrigen().getId());
        assertEquals(20L, flight.getCiudadDestino().getId());
    }

    @Test
    void testAllPricesAssignment() {
        Flight flight = new Flight();
        flight.setPrecioEconomica(new BigDecimal("100"));
        flight.setPrecioEjecutiva(new BigDecimal("200"));
        flight.setPrecioPrimeraClase(new BigDecimal("300"));
        
        assertEquals(new BigDecimal("100"), flight.getPrecioEconomica());
        assertEquals(new BigDecimal("200"), flight.getPrecioEjecutiva());
        assertEquals(new BigDecimal("300"), flight.getPrecioPrimeraClase());
    }

    @Test
    void testAllSeatsAssignment() {
        Flight flight = new Flight();
        flight.setAsientosDisponiblesEconomica(150);
        flight.setAsientosDisponiblesEjecutiva(30);
        flight.setAsientosDisponiblesPrimeraClase(12);
        
        assertEquals(150, flight.getAsientosDisponiblesEconomica());
        assertEquals(30, flight.getAsientosDisponiblesEjecutiva());
        assertEquals(12, flight.getAsientosDisponiblesPrimeraClase());
    }
}
