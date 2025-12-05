package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SeatWithLockInfoTest {

    @Test
    void testSeatWithLockInfoConstructor() {
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setVueloId(100L);
        seat.setNumeroAsiento("A1");
        seat.setFila(1);
        seat.setColumna("A");
        seat.setClase("Economica");
        seat.setUbicacion("ventana");
        seat.setDisponible(true);
        seat.setPrecio(new BigDecimal("500.00"));
        
        SeatWithLockInfo info = new SeatWithLockInfo(seat, true, 300, "user-123");
        
        assertEquals(1L, info.getId());
        assertEquals(100L, info.getVueloId());
        assertEquals("A1", info.getNumeroAsiento());
        assertTrue(info.isLocked());
        assertEquals(300, info.getRemainingLockSeconds());
        assertEquals("user-123", info.getLockedByUserId());
    }

    @Test
    void testGetSetId() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setId(50L);
        assertEquals(50L, info.getId());
    }

    @Test
    void testGetSetVueloId() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setVueloId(200L);
        assertEquals(200L, info.getVueloId());
    }

    @Test
    void testGetSetNumeroAsiento() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setNumeroAsiento("B5");
        assertEquals("B5", info.getNumeroAsiento());
    }

    @Test
    void testGetSetFila() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setFila(10);
        assertEquals(10, info.getFila());
    }

    @Test
    void testGetSetColumna() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setColumna("C");
        assertEquals("C", info.getColumna());
    }

    @Test
    void testGetSetClase() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setClase("Primera");
        assertEquals("Primera", info.getClase());
    }

    @Test
    void testGetSetUbicacion() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setUbicacion("pasillo");
        assertEquals("pasillo", info.getUbicacion());
    }

    @Test
    void testGetSetDisponible() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setDisponible(false);
        assertFalse(info.getDisponible());
    }

    @Test
    void testGetSetPrecio() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        BigDecimal precio = new BigDecimal("750.00");
        info.setPrecio(precio);
        assertEquals(precio, info.getPrecio());
    }

    @Test
    void testGetSetFechaCreacion() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        LocalDateTime fecha = LocalDateTime.now();
        info.setFechaCreacion(fecha);
        assertEquals(fecha, info.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        LocalDateTime fecha = LocalDateTime.now();
        info.setFechaActualizacion(fecha);
        assertEquals(fecha, info.getFechaActualizacion());
    }

    @Test
    void testGetSetLocked() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setLocked(true);
        assertTrue(info.isLocked());
    }

    @Test
    void testGetSetRemainingLockSeconds() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setRemainingLockSeconds(600);
        assertEquals(600, info.getRemainingLockSeconds());
    }

    @Test
    void testGetSetLockedByUserId() {
        Seat seat = new Seat();
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        info.setLockedByUserId("user-456");
        assertEquals("user-456", info.getLockedByUserId());
    }

    @Test
    void testSeatWithLockInfoNotLocked() {
        Seat seat = new Seat();
        seat.setId(2L);
        seat.setDisponible(true);
        
        SeatWithLockInfo info = new SeatWithLockInfo(seat, false, 0, null);
        
        assertFalse(info.isLocked());
        assertEquals(0, info.getRemainingLockSeconds());
        assertNull(info.getLockedByUserId());
    }

    @Test
    void testSeatWithLockInfoLocked() {
        Seat seat = new Seat();
        seat.setId(3L);
        
        SeatWithLockInfo info = new SeatWithLockInfo(seat, true, 900, "user-789");
        
        assertTrue(info.isLocked());
        assertEquals(900, info.getRemainingLockSeconds());
        assertEquals("user-789", info.getLockedByUserId());
    }

    @Test
    void testSeatWithAllProperties() {
        Seat seat = new Seat();
        seat.setId(5L);
        seat.setVueloId(500L);
        seat.setNumeroAsiento("D10");
        seat.setFila(10);
        seat.setColumna("D");
        seat.setClase("Ejecutiva");
        seat.setUbicacion("centro");
        seat.setDisponible(false);
        seat.setPrecio(new BigDecimal("1200.00"));
        LocalDateTime now = LocalDateTime.now();
        seat.setFechaCreacion(now);
        seat.setFechaActualizacion(now);
        
        SeatWithLockInfo info = new SeatWithLockInfo(seat, true, 450, "user-abc");
        
        assertEquals(5L, info.getId());
        assertEquals(500L, info.getVueloId());
        assertEquals("D10", info.getNumeroAsiento());
        assertEquals(10, info.getFila());
        assertEquals("D", info.getColumna());
        assertEquals("Ejecutiva", info.getClase());
        assertEquals("centro", info.getUbicacion());
        assertFalse(info.getDisponible());
        assertEquals(new BigDecimal("1200.00"), info.getPrecio());
        assertEquals(now, info.getFechaCreacion());
        assertEquals(now, info.getFechaActualizacion());
        assertTrue(info.isLocked());
        assertEquals(450, info.getRemainingLockSeconds());
        assertEquals("user-abc", info.getLockedByUserId());
    }
}
