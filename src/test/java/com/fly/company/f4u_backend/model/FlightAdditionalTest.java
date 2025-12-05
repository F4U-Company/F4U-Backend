package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class FlightAdditionalTest {

    @Test
    void testSetMultipleCities() {
        Flight flight = new Flight();
        City origin1 = new City();
        origin1.setId(1L);
        City origin2 = new City();
        origin2.setId(2L);
        
        flight.setCiudadOrigen(origin1);
        assertEquals(1L, flight.getCiudadOrigen().getId());
        
        flight.setCiudadOrigen(origin2);
        assertEquals(2L, flight.getCiudadOrigen().getId());
    }

    @Test
    void testZeroPrices() {
        Flight flight = new Flight();
        flight.setPrecioEconomica(BigDecimal.ZERO);
        flight.setPrecioEjecutiva(BigDecimal.ZERO);
        flight.setPrecioPrimeraClase(BigDecimal.ZERO);
        
        assertEquals(BigDecimal.ZERO, flight.getPrecioEconomica());
        assertEquals(BigDecimal.ZERO, flight.getPrecioEjecutiva());
        assertEquals(BigDecimal.ZERO, flight.getPrecioPrimeraClase());
    }

    @Test
    void testZeroSeats() {
        Flight flight = new Flight();
        flight.setAsientosDisponiblesEconomica(0);
        flight.setAsientosDisponiblesEjecutiva(0);
        flight.setAsientosDisponiblesPrimeraClase(0);
        
        assertEquals(0, flight.getAsientosDisponiblesEconomica());
        assertEquals(0, flight.getAsientosDisponiblesEjecutiva());
        assertEquals(0, flight.getAsientosDisponiblesPrimeraClase());
    }

    @Test
    void testDifferentEstados() {
        Flight flight = new Flight();
        
        flight.setEstado("PROGRAMADO");
        assertEquals("PROGRAMADO", flight.getEstado());
        
        flight.setEstado("EN_VUELO");
        assertEquals("EN_VUELO", flight.getEstado());
        
        flight.setEstado("ATERRIZADO");
        assertEquals("ATERRIZADO", flight.getEstado());
        
        flight.setEstado("CANCELADO");
        assertEquals("CANCELADO", flight.getEstado());
    }

    @Test
    void testNullCities() {
        Flight flight = new Flight();
        flight.setCiudadOrigen(null);
        flight.setCiudadDestino(null);
        
        assertNull(flight.getCiudadOrigen());
        assertNull(flight.getCiudadDestino());
    }

    @Test
    void testEmptyTerminalAndGate() {
        Flight flight = new Flight();
        flight.setTerminal("");
        flight.setPuertaEmbarque("");
        
        assertEquals("", flight.getTerminal());
        assertEquals("", flight.getPuertaEmbarque());
    }

    @Test
    void testNullPrices() {
        Flight flight = new Flight();
        flight.setPrecioEconomica(null);
        flight.setPrecioEjecutiva(null);
        flight.setPrecioPrimeraClase(null);
        
        assertNull(flight.getPrecioEconomica());
        assertNull(flight.getPrecioEjecutiva());
        assertNull(flight.getPrecioPrimeraClase());
    }

    @Test
    void testLargePrices() {
        Flight flight = new Flight();
        BigDecimal largePrice = new BigDecimal("99999999.99");
        
        flight.setPrecioEconomica(largePrice);
        flight.setPrecioEjecutiva(largePrice);
        flight.setPrecioPrimeraClase(largePrice);
        
        assertEquals(largePrice, flight.getPrecioEconomica());
        assertEquals(largePrice, flight.getPrecioEjecutiva());
        assertEquals(largePrice, flight.getPrecioPrimeraClase());
    }

    @Test
    void testLargeSeatsNumber() {
        Flight flight = new Flight();
        
        flight.setAsientosDisponiblesEconomica(999);
        flight.setAsientosDisponiblesEjecutiva(999);
        flight.setAsientosDisponiblesPrimeraClase(999);
        
        assertEquals(999, flight.getAsientosDisponiblesEconomica());
        assertEquals(999, flight.getAsientosDisponiblesEjecutiva());
        assertEquals(999, flight.getAsientosDisponiblesPrimeraClase());
    }

    @Test
    void testCompleteFlightData() {
        Flight flight = new Flight();
        City origin = new City();
        origin.setId(100L);
        City dest = new City();
        dest.setId(200L);
        
        flight.setId(1000L);
        flight.setNumeroVuelo("FL9999");
        flight.setAerolineaId(50L);
        flight.setAvionId(75L);
        flight.setCiudadOrigen(origin);
        flight.setCiudadDestino(dest);
        flight.setFechaSalida(LocalDateTime.now());
        flight.setFechaLlegada(LocalDateTime.now().plusHours(3));
        flight.setDuracionMinutos(180);
        flight.setPrecioEconomica(new BigDecimal("500"));
        flight.setPrecioEjecutiva(new BigDecimal("1000"));
        flight.setPrecioPrimeraClase(new BigDecimal("2000"));
        flight.setAsientosDisponiblesEconomica(150);
        flight.setAsientosDisponiblesEjecutiva(30);
        flight.setAsientosDisponiblesPrimeraClase(10);
        flight.setEstado("PROGRAMADO");
        flight.setPuertaEmbarque("A10");
        flight.setTerminal("T2");
        flight.setActivo(true);
        
        assertEquals(1000L, flight.getId());
        assertEquals("FL9999", flight.getNumeroVuelo());
        assertEquals(50L, flight.getAerolineaId());
        assertEquals(75L, flight.getAvionId());
        assertEquals(100L, flight.getCiudadOrigen().getId());
        assertEquals(200L, flight.getCiudadDestino().getId());
        assertEquals(180, flight.getDuracionMinutos());
        assertTrue(flight.getActivo());
    }
}
