package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReservationAdditionalFieldsTest {

    @Test
    void testGetSetExtra() {
        Reservation r = new Reservation();
        Extra extra = new Extra();
        extra.setId(100L);
        r.setExtra(extra);
        assertEquals(extra, r.getExtra());
        assertEquals(100L, r.getExtra().getId());
    }

    @Test
    void testGetSetFechaCreacion() {
        Reservation r = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        r.setFechaCreacion(now);
        assertEquals(now, r.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        Reservation r = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        r.setFechaActualizacion(now);
        assertEquals(now, r.getFechaActualizacion());
    }

    @Test
    void testNullExtra() {
        Reservation r = new Reservation();
        r.setExtra(null);
        assertNull(r.getExtra());
    }

    @Test
    void testNullFechas() {
        Reservation r = new Reservation();
        r.setFechaReservacion(null);
        r.setFechaPago(null);
        r.setFechaCheckIn(null);
        r.setFechaCancelacion(null);
        r.setFechaVencimientoPago(null);
        
        assertNull(r.getFechaReservacion());
        assertNull(r.getFechaPago());
        assertNull(r.getFechaCheckIn());
        assertNull(r.getFechaCancelacion());
        assertNull(r.getFechaVencimientoPago());
    }

    @Test
    void testNullPrecio() {
        Reservation r = new Reservation();
        r.setPrecioExtras(null);
        assertNull(r.getPrecioExtras());
    }

    @Test
    void testEmptyStrings() {
        Reservation r = new Reservation();
        r.setCodigoReservacion("");
        r.setPasajeroTelefono("");
        r.setPasajeroDocumentoTipo("");
        r.setPasajeroDocumentoNumero("");
        r.setMetodoPago("");
        r.setReferenciaPago("");
        r.setEstadoPago("");
        r.setObservaciones("");
        r.setOrigenReserva("");
        
        assertEquals("", r.getCodigoReservacion());
        assertEquals("", r.getPasajeroTelefono());
        assertEquals("", r.getPasajeroDocumentoTipo());
        assertEquals("", r.getPasajeroDocumentoNumero());
        assertEquals("", r.getMetodoPago());
        assertEquals("", r.getReferenciaPago());
        assertEquals("", r.getEstadoPago());
        assertEquals("", r.getObservaciones());
        assertEquals("", r.getOrigenReserva());
    }

    @Test
    void testAllDatesSet() {
        Reservation r = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        
        r.setFechaReservacion(now);
        r.setFechaPago(future);
        r.setFechaCheckIn(future.plusDays(1));
        r.setFechaCancelacion(null);
        r.setFechaVencimientoPago(future.plusDays(2));
        r.setFechaCreacion(now);
        r.setFechaActualizacion(now);
        
        assertEquals(now, r.getFechaReservacion());
        assertEquals(future, r.getFechaPago());
        assertNotNull(r.getFechaCheckIn());
        assertNull(r.getFechaCancelacion());
        assertNotNull(r.getFechaVencimientoPago());
        assertEquals(now, r.getFechaCreacion());
        assertEquals(now, r.getFechaActualizacion());
    }

    @Test
    void testMixedExtras() {
        Reservation r = new Reservation();
        r.setExtraMaletaCabina(true);
        r.setExtraMaletaBodega(false);
        r.setExtraSeguro50(true);
        r.setExtraSeguro100(false);
        r.setExtraAsistenciaEspecial(true);
        
        assertTrue(r.getExtraMaletaCabina());
        assertFalse(r.getExtraMaletaBodega());
        assertTrue(r.getExtraSeguro50());
        assertFalse(r.getExtraSeguro100());
        assertTrue(r.getExtraAsistenciaEspecial());
    }

    @Test
    void testBigDecimalPrices() {
        Reservation r = new Reservation();
        BigDecimal precioAsiento = new BigDecimal("1500.50");
        BigDecimal precioExtras = new BigDecimal("200.75");
        BigDecimal precioTotal = new BigDecimal("1701.25");
        
        r.setPrecioAsiento(precioAsiento);
        r.setPrecioExtras(precioExtras);
        r.setPrecioTotal(precioTotal);
        
        assertEquals(precioAsiento, r.getPrecioAsiento());
        assertEquals(precioExtras, r.getPrecioExtras());
        assertEquals(precioTotal, r.getPrecioTotal());
    }
}
