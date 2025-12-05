package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SeatAdditionalTest {

    @Test
    void testNullNumeroAsiento() {
        Seat seat = new Seat();
        seat.setNumeroAsiento(null);
        assertNull(seat.getNumeroAsiento());
    }

    @Test
    void testNullFila() {
        Seat seat = new Seat();
        seat.setFila(null);
        assertNull(seat.getFila());
    }

    @Test
    void testNullColumna() {
        Seat seat = new Seat();
        seat.setColumna(null);
        assertNull(seat.getColumna());
    }

    @Test
    void testNullClase() {
        Seat seat = new Seat();
        seat.setClase(null);
        assertNull(seat.getClase());
    }

    @Test
    void testNullUbicacion() {
        Seat seat = new Seat();
        seat.setUbicacion(null);
        assertNull(seat.getUbicacion());
    }

    @Test
    void testNullDisponible() {
        Seat seat = new Seat();
        seat.setDisponible(null);
        assertNull(seat.getDisponible());
    }

    @Test
    void testNullPrecio() {
        Seat seat = new Seat();
        seat.setPrecio(null);
        assertNull(seat.getPrecio());
    }

    @Test
    void testDifferentColumns() {
        Seat seat = new Seat();
        String[] columnas = {"A", "B", "C", "D", "E", "F", "G", "H"};
        
        for (String col : columnas) {
            seat.setColumna(col);
            assertEquals(col, seat.getColumna());
        }
    }

    @Test
    void testDifferentRows() {
        Seat seat = new Seat();
        for (int i = 1; i <= 50; i++) {
            seat.setFila(i);
            assertEquals(i, seat.getFila());
        }
    }

    @Test
    void testPriceZero() {
        Seat seat = new Seat();
        seat.setPrecio(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, seat.getPrecio());
    }

    @Test
    void testPriceLarge() {
        Seat seat = new Seat();
        BigDecimal largePrice = new BigDecimal("9999999.99");
        seat.setPrecio(largePrice);
        assertEquals(largePrice, seat.getPrecio());
    }

    @Test
    void testSeatUnavailable() {
        Seat seat = new Seat();
        seat.setDisponible(false);
        assertFalse(seat.getDisponible());
    }

    @Test
    void testChangingAvailability() {
        Seat seat = new Seat();
        seat.setDisponible(true);
        assertTrue(seat.getDisponible());
        
        seat.setDisponible(false);
        assertFalse(seat.getDisponible());
        
        seat.setDisponible(true);
        assertTrue(seat.getDisponible());
    }

    @Test
    void testDateTimeFields() {
        Seat seat = new Seat();
        LocalDateTime now = LocalDateTime.now();
        
        seat.setFechaCreacion(now);
        seat.setFechaActualizacion(now);
        
        assertEquals(now, seat.getFechaCreacion());
        assertEquals(now, seat.getFechaActualizacion());
    }

    @Test
    void testCompleteSeat() {
        Seat seat = new Seat();
        LocalDateTime now = LocalDateTime.now();
        
        seat.setId(999L);
        seat.setVueloId(500L);
        seat.setNumeroAsiento("50F");
        seat.setFila(50);
        seat.setColumna("F");
        seat.setClase("PRIMERA_CLASE");
        seat.setUbicacion("VENTANA");
        seat.setDisponible(false);
        seat.setPrecio(new BigDecimal("999.99"));
        seat.setFechaCreacion(now);
        seat.setFechaActualizacion(now);
        
        assertEquals(999L, seat.getId());
        assertEquals(500L, seat.getVueloId());
        assertEquals("50F", seat.getNumeroAsiento());
        assertEquals(50, seat.getFila());
        assertEquals("F", seat.getColumna());
        assertEquals("PRIMERA_CLASE", seat.getClase());
        assertEquals("VENTANA", seat.getUbicacion());
        assertFalse(seat.getDisponible());
        assertEquals(new BigDecimal("999.99"), seat.getPrecio());
        assertEquals(now, seat.getFechaCreacion());
        assertEquals(now, seat.getFechaActualizacion());
    }
}
