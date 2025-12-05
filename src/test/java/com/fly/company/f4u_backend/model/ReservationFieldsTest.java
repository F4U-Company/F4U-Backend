package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReservationFieldsTest {

    @Test
    void testReservationDefaultConstructor() {
        Reservation reservation = new Reservation();
        assertNotNull(reservation);
    }

    @Test
    void testGetSetCodigoReservacion() {
        Reservation r = new Reservation();
        r.setCodigoReservacion("RES123456");
        assertEquals("RES123456", r.getCodigoReservacion());
    }

    @Test
    void testGetSetVueloId() {
        Reservation r = new Reservation();
        r.setVueloId(100L);
        assertEquals(100L, r.getVueloId());
    }

    @Test
    void testGetSetPasajeroId() {
        Reservation r = new Reservation();
        r.setPasajeroId(50L);
        assertEquals(50L, r.getPasajeroId());
    }

    @Test
    void testGetSetAsientoId() {
        Reservation r = new Reservation();
        r.setAsientoId(25L);
        assertEquals(25L, r.getAsientoId());
    }

    @Test
    void testGetSetPasajeroTelefono() {
        Reservation r = new Reservation();
        r.setPasajeroTelefono("1234567890");
        assertEquals("1234567890", r.getPasajeroTelefono());
    }

    @Test
    void testGetSetPasajeroDocumentoTipo() {
        Reservation r = new Reservation();
        r.setPasajeroDocumentoTipo("DNI");
        assertEquals("DNI", r.getPasajeroDocumentoTipo());
    }

    @Test
    void testGetSetPasajeroDocumentoNumero() {
        Reservation r = new Reservation();
        r.setPasajeroDocumentoNumero("12345678");
        assertEquals("12345678", r.getPasajeroDocumentoNumero());
    }

    @Test
    void testGetSetPasajeroFechaNacimiento() {
        Reservation r = new Reservation();
        LocalDate fecha = LocalDate.of(1990, 5, 15);
        r.setPasajeroFechaNacimiento(fecha);
        assertEquals(fecha, r.getPasajeroFechaNacimiento());
    }

    @Test
    void testGetSetPrecioExtras() {
        Reservation r = new Reservation();
        BigDecimal precio = new BigDecimal("50.00");
        r.setPrecioExtras(precio);
        assertEquals(precio, r.getPrecioExtras());
    }

    @Test
    void testGetSetExtraMaletaCabina() {
        Reservation r = new Reservation();
        r.setExtraMaletaCabina(true);
        assertTrue(r.getExtraMaletaCabina());
    }

    @Test
    void testGetSetExtraMaletaBodega() {
        Reservation r = new Reservation();
        r.setExtraMaletaBodega(true);
        assertTrue(r.getExtraMaletaBodega());
    }

    @Test
    void testGetSetExtraSeguro50() {
        Reservation r = new Reservation();
        r.setExtraSeguro50(true);
        assertTrue(r.getExtraSeguro50());
    }

    @Test
    void testGetSetExtraSeguro100() {
        Reservation r = new Reservation();
        r.setExtraSeguro100(true);
        assertTrue(r.getExtraSeguro100());
    }

    @Test
    void testGetSetExtraAsistenciaEspecial() {
        Reservation r = new Reservation();
        r.setExtraAsistenciaEspecial(true);
        assertTrue(r.getExtraAsistenciaEspecial());
    }

    @Test
    void testGetSetMetodoPago() {
        Reservation r = new Reservation();
        r.setMetodoPago("TARJETA");
        assertEquals("TARJETA", r.getMetodoPago());
    }

    @Test
    void testGetSetReferenciaPago() {
        Reservation r = new Reservation();
        r.setReferenciaPago("REF123456");
        assertEquals("REF123456", r.getReferenciaPago());
    }

    @Test
    void testGetSetEstadoPago() {
        Reservation r = new Reservation();
        r.setEstadoPago("APROBADO");
        assertEquals("APROBADO", r.getEstadoPago());
    }

    @Test
    void testGetSetFechaReservacion() {
        Reservation r = new Reservation();
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaReservacion(fecha);
        assertEquals(fecha, r.getFechaReservacion());
    }

    @Test
    void testGetSetFechaPago() {
        Reservation r = new Reservation();
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaPago(fecha);
        assertEquals(fecha, r.getFechaPago());
    }

    @Test
    void testGetSetFechaCheckIn() {
        Reservation r = new Reservation();
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaCheckIn(fecha);
        assertEquals(fecha, r.getFechaCheckIn());
    }

    @Test
    void testGetSetFechaCancelacion() {
        Reservation r = new Reservation();
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaCancelacion(fecha);
        assertEquals(fecha, r.getFechaCancelacion());
    }

    @Test
    void testGetSetFechaVencimientoPago() {
        Reservation r = new Reservation();
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaVencimientoPago(fecha);
        assertEquals(fecha, r.getFechaVencimientoPago());
    }

    @Test
    void testGetSetObservaciones() {
        Reservation r = new Reservation();
        r.setObservaciones("Observación de prueba");
        assertEquals("Observación de prueba", r.getObservaciones());
    }

    @Test
    void testGetSetOrigenReserva() {
        Reservation r = new Reservation();
        r.setOrigenReserva("MOBILE");
        assertEquals("MOBILE", r.getOrigenReserva());
    }

    @Test
    void testAllExtrasEnabled() {
        Reservation r = new Reservation();
        r.setExtraMaletaCabina(true);
        r.setExtraMaletaBodega(true);
        r.setExtraSeguro50(true);
        r.setExtraSeguro100(true);
        r.setExtraAsistenciaEspecial(true);
        
        assertTrue(r.getExtraMaletaCabina());
        assertTrue(r.getExtraMaletaBodega());
        assertTrue(r.getExtraSeguro50());
        assertTrue(r.getExtraSeguro100());
        assertTrue(r.getExtraAsistenciaEspecial());
    }

    @Test
    void testAllExtrasDisabled() {
        Reservation r = new Reservation();
        r.setExtraMaletaCabina(false);
        r.setExtraMaletaBodega(false);
        r.setExtraSeguro50(false);
        r.setExtraSeguro100(false);
        r.setExtraAsistenciaEspecial(false);
        
        assertFalse(r.getExtraMaletaCabina());
        assertFalse(r.getExtraMaletaBodega());
        assertFalse(r.getExtraSeguro50());
        assertFalse(r.getExtraSeguro100());
        assertFalse(r.getExtraAsistenciaEspecial());
    }
}
