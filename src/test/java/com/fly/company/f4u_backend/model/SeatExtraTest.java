package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SeatExtraTest {

    @Test
    void testSeatCreation() {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setNumeroAsiento("1A");
        
        assertNotNull(seat);
        assertEquals(1L, seat.getId());
        assertEquals("1A", seat.getNumeroAsiento());
    }

    @Test
    void testSeatPrice() {
        Seat seat = new Seat();
        BigDecimal price = new BigDecimal("150000");
        seat.setPrecio(price);
        
        assertEquals(price, seat.getPrecio());
    }

    @Test
    void testSeatAvailability() {
        Seat seat = new Seat();
        seat.setDisponible(true);
        
        assertTrue(seat.getDisponible());
        
        seat.setDisponible(false);
        assertFalse(seat.getDisponible());
    }

    @Test
    void testSeatClass() {
        Seat seat = new Seat();
        seat.setClase("ECONOMICA");
        
        assertEquals("ECONOMICA", seat.getClase());
    }

    @Test
    void testSeatFlightId() {
        Seat seat = new Seat();
        seat.setVueloId(100L);
        
        assertEquals(100L, seat.getVueloId());
    }

    @Test
    void testSeatAllFields() {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setNumeroAsiento("12B");
        seat.setClase("EJECUTIVA");
        seat.setDisponible(true);
        seat.setPrecio(new BigDecimal("350000"));
        seat.setVueloId(200L);
        
        assertEquals(1L, seat.getId());
        assertEquals("12B", seat.getNumeroAsiento());
        assertEquals("EJECUTIVA", seat.getClase());
        assertTrue(seat.getDisponible());
        assertEquals(new BigDecimal("350000"), seat.getPrecio());
        assertEquals(200L, seat.getVueloId());
    }

    @Test
    void testSeatRowColumn() {
        Seat seat = new Seat();
        seat.setFila(10);
        seat.setColumna("A");
        
        assertEquals(10, seat.getFila());
        assertEquals("A", seat.getColumna());
    }

    @Test
    void testSeatDifferentClasses() {
        Seat economica = new Seat();
        economica.setClase("ECONOMICA");
        
        Seat ejecutiva = new Seat();
        ejecutiva.setClase("EJECUTIVA");
        
        Seat primera = new Seat();
        primera.setClase("PRIMERA_CLASE");
        
        assertEquals("ECONOMICA", economica.getClase());
        assertEquals("EJECUTIVA", ejecutiva.getClase());
        assertEquals("PRIMERA_CLASE", primera.getClase());
    }
}
