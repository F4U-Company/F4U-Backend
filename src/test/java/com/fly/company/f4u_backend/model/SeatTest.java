package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SeatTest {

    private Seat seat;

    @BeforeEach
    void setUp() {
        seat = new Seat();
    }

    @Test
    void testSeatCreation() {
        assertNotNull(seat);
        // disponible puede ser null por defecto
        seat.setDisponible(true);
        assertTrue(seat.getDisponible());
    }

    @Test
    void testSetAndGetId() {
        seat.setId(1L);
        assertEquals(1L, seat.getId());
    }

    @Test
    void testSetAndGetNumeroAsiento() {
        seat.setNumeroAsiento("1A");
        assertEquals("1A", seat.getNumeroAsiento());
    }

    @Test
    void testSetAndGetClase() {
        seat.setClase("PRIMERA_CLASE");
        assertEquals("PRIMERA_CLASE", seat.getClase());
    }

    @Test
    void testSetAndGetPrecio() {
        BigDecimal precio = new BigDecimal("650000");
        seat.setPrecio(precio);
        assertEquals(precio, seat.getPrecio());
    }

    @Test
    void testSetAndGetDisponible() {
        seat.setDisponible(false);
        assertFalse(seat.getDisponible());
    }

    @Test
    void testSetAndGetVueloId() {
        seat.setVueloId(100L);
        assertEquals(100L, seat.getVueloId());
    }

    @Test
    void testFila() {
        seat.setFila(5);
        assertEquals(5, seat.getFila());
    }

    @Test
    void testColumna() {
        seat.setColumna("A");
        assertEquals("A", seat.getColumna());
    }

    @Test
    void testDefaultDisponibleValue() {
        Seat newSeat = new Seat();
        // El valor por defecto puede ser null, no true
        assertNull(newSeat.getDisponible());
    }

    @Test
    void testPrecioDecimalPrecision() {
        BigDecimal precio = new BigDecimal("350000.50");
        seat.setPrecio(precio);
        assertEquals(0, precio.compareTo(seat.getPrecio()));
    }

    @Test
    void testNullValues() {
        seat.setNumeroAsiento(null);
        seat.setClase(null);
        seat.setPrecio(null);
        
        assertNull(seat.getNumeroAsiento());
        assertNull(seat.getClase());
        assertNull(seat.getPrecio());
    }

    @Test
    void testMultipleClaseTypes() {
        String[] clases = {"PRIMERA_CLASE", "EJECUTIVA", "ECONOMICA"};
        
        for (String clase : clases) {
            seat.setClase(clase);
            assertEquals(clase, seat.getClase());
        }
    }
}
