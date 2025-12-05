package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SeatFieldsTest {

    @Test
    void testSeatDefaultConstructor() {
        Seat seat = new Seat();
        assertNotNull(seat);
    }

    @Test
    void testGetSetVueloId() {
        Seat seat = new Seat();
        seat.setVueloId(100L);
        assertEquals(100L, seat.getVueloId());
    }

    @Test
    void testGetSetNumeroAsiento() {
        Seat seat = new Seat();
        seat.setNumeroAsiento("12A");
        assertEquals("12A", seat.getNumeroAsiento());
    }

    @Test
    void testGetSetFila() {
        Seat seat = new Seat();
        seat.setFila(15);
        assertEquals(15, seat.getFila());
    }

    @Test
    void testGetSetColumna() {
        Seat seat = new Seat();
        seat.setColumna("B");
        assertEquals("B", seat.getColumna());
    }

    @Test
    void testGetSetClase() {
        Seat seat = new Seat();
        seat.setClase("ECONOMICA");
        assertEquals("ECONOMICA", seat.getClase());
    }

    @Test
    void testGetSetUbicacion() {
        Seat seat = new Seat();
        seat.setUbicacion("VENTANA");
        assertEquals("VENTANA", seat.getUbicacion());
    }

    @Test
    void testGetSetDisponible() {
        Seat seat = new Seat();
        seat.setDisponible(true);
        assertTrue(seat.getDisponible());
        seat.setDisponible(false);
        assertFalse(seat.getDisponible());
    }

    @Test
    void testGetSetPrecio() {
        Seat seat = new Seat();
        BigDecimal precio = new BigDecimal("150.50");
        seat.setPrecio(precio);
        assertEquals(precio, seat.getPrecio());
    }

    @Test
    void testGetSetFechaCreacion() {
        Seat seat = new Seat();
        LocalDateTime now = LocalDateTime.now();
        seat.setFechaCreacion(now);
        assertEquals(now, seat.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        Seat seat = new Seat();
        LocalDateTime now = LocalDateTime.now();
        seat.setFechaActualizacion(now);
        assertEquals(now, seat.getFechaActualizacion());
    }

    @Test
    void testSeatWithAllFields() {
        Seat seat = new Seat();
        seat.setId(200L);
        seat.setVueloId(50L);
        seat.setNumeroAsiento("10C");
        seat.setFila(10);
        seat.setColumna("C");
        seat.setClase("EJECUTIVA");
        seat.setUbicacion("PASILLO");
        seat.setDisponible(true);
        seat.setPrecio(new BigDecimal("300.00"));
        
        assertEquals(200L, seat.getId());
        assertEquals(50L, seat.getVueloId());
        assertEquals("10C", seat.getNumeroAsiento());
        assertEquals(10, seat.getFila());
        assertEquals("C", seat.getColumna());
        assertEquals("EJECUTIVA", seat.getClase());
        assertEquals("PASILLO", seat.getUbicacion());
        assertTrue(seat.getDisponible());
        assertEquals(new BigDecimal("300.00"), seat.getPrecio());
    }

    @Test
    void testSeatDifferentClasses() {
        Seat s1 = new Seat();
        s1.setClase("ECONOMICA");
        
        Seat s2 = new Seat();
        s2.setClase("EJECUTIVA");
        
        Seat s3 = new Seat();
        s3.setClase("PRIMERA_CLASE");
        
        assertEquals("ECONOMICA", s1.getClase());
        assertEquals("EJECUTIVA", s2.getClase());
        assertEquals("PRIMERA_CLASE", s3.getClase());
    }

    @Test
    void testSeatDifferentUbicaciones() {
        Seat s1 = new Seat();
        s1.setUbicacion("VENTANA");
        
        Seat s2 = new Seat();
        s2.setUbicacion("MEDIO");
        
        Seat s3 = new Seat();
        s3.setUbicacion("PASILLO");
        
        assertEquals("VENTANA", s1.getUbicacion());
        assertEquals("MEDIO", s2.getUbicacion());
        assertEquals("PASILLO", s3.getUbicacion());
    }

    @Test
    void testSeatNumberAssignments() {
        Seat seat = new Seat();
        String[] numeros = {"1A", "5B", "10C", "15D", "20E", "25F"};
        
        for (String numero : numeros) {
            seat.setNumeroAsiento(numero);
            assertEquals(numero, seat.getNumeroAsiento());
        }
    }
}
