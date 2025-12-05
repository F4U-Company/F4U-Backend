package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReservationRequestFieldsTest {

    @Test
    void testReservationRequestDefaultConstructor() {
        ReservationRequest req = new ReservationRequest();
        assertNotNull(req);
    }

    @Test
    void testGetSetLockUserId() {
        ReservationRequest req = new ReservationRequest();
        req.setLockUserId("user123");
        assertEquals("user123", req.getLockUserId());
    }

    @Test
    void testAllPriceFields() {
        ReservationRequest req = new ReservationRequest();
        req.setPrecioAsiento(new BigDecimal("200.00"));
        req.setPrecioExtras(new BigDecimal("50.00"));
        req.setPrecioTotal(new BigDecimal("250.00"));
        
        assertEquals(new BigDecimal("200.00"), req.getPrecioAsiento());
        assertEquals(new BigDecimal("50.00"), req.getPrecioExtras());
        assertEquals(new BigDecimal("250.00"), req.getPrecioTotal());
    }

    @Test
    void testAllPaymentFields() {
        ReservationRequest req = new ReservationRequest();
        req.setMetodoPago("TARJETA");
        req.setReferenciaPago("REF123");
        req.setEstadoPago("APROBADO");
        
        assertEquals("TARJETA", req.getMetodoPago());
        assertEquals("REF123", req.getReferenciaPago());
        assertEquals("APROBADO", req.getEstadoPago());
    }

    @Test
    void testEstadoField() {
        ReservationRequest req = new ReservationRequest();
        req.setEstado("CONFIRMADA");
        assertEquals("CONFIRMADA", req.getEstado());
    }

    @Test
    void testOrigenReservaField() {
        ReservationRequest req = new ReservationRequest();
        req.setOrigenReserva("WEB");
        assertEquals("WEB", req.getOrigenReserva());
        
        req.setOrigenReserva("MOBILE");
        assertEquals("MOBILE", req.getOrigenReserva());
    }

    @Test
    void testObservacionesField() {
        ReservationRequest req = new ReservationRequest();
        req.setObservaciones("Observación de prueba");
        assertEquals("Observación de prueba", req.getObservaciones());
    }

    @Test
    void testCompleteReservationRequest() {
        ReservationRequest req = new ReservationRequest();
        
        req.setVueloId(100L);
        req.setAsientoId(50L);
        req.setPasajeroId(25L);
        req.setLockUserId("lock123");
        req.setPasajeroNombre("Juan");
        req.setPasajeroApellido("Pérez");
        req.setPasajeroEmail("juan@test.com");
        req.setPasajeroTelefono("123456789");
        req.setPasajeroDocumentoTipo("DNI");
        req.setPasajeroDocumentoNumero("87654321");
        req.setPasajeroFechaNacimiento(LocalDate.of(1990, 5, 15));
        req.setClase("ECONOMICA");
        req.setPrecioAsiento(new BigDecimal("200.00"));
        req.setPrecioExtras(new BigDecimal("50.00"));
        req.setPrecioTotal(new BigDecimal("250.00"));
        req.setExtraMaletaCabina(true);
        req.setExtraMaletaBodega(true);
        req.setExtraSeguro50(false);
        req.setExtraSeguro100(true);
        req.setExtraAsistenciaEspecial(false);
        req.setMetodoPago("TARJETA");
        req.setReferenciaPago("REF123456");
        req.setEstadoPago("APROBADO");
        req.setEstado("CONFIRMADA");
        req.setObservaciones("Sin observaciones");
        req.setOrigenReserva("WEB");
        
        assertEquals(100L, req.getVueloId());
        assertEquals(50L, req.getAsientoId());
        assertEquals(25L, req.getPasajeroId());
        assertEquals("lock123", req.getLockUserId());
        assertEquals("Juan", req.getPasajeroNombre());
        assertEquals("Pérez", req.getPasajeroApellido());
        assertEquals("juan@test.com", req.getPasajeroEmail());
        assertEquals("123456789", req.getPasajeroTelefono());
        assertEquals("DNI", req.getPasajeroDocumentoTipo());
        assertEquals("87654321", req.getPasajeroDocumentoNumero());
        assertEquals(LocalDate.of(1990, 5, 15), req.getPasajeroFechaNacimiento());
        assertEquals("ECONOMICA", req.getClase());
        assertTrue(req.getExtraMaletaCabina());
        assertTrue(req.getExtraMaletaBodega());
        assertFalse(req.getExtraSeguro50());
        assertTrue(req.getExtraSeguro100());
        assertFalse(req.getExtraAsistenciaEspecial());
        assertEquals("TARJETA", req.getMetodoPago());
        assertEquals("REF123456", req.getReferenciaPago());
        assertEquals("APROBADO", req.getEstadoPago());
        assertEquals("CONFIRMADA", req.getEstado());
        assertEquals("Sin observaciones", req.getObservaciones());
        assertEquals("WEB", req.getOrigenReserva());
    }
}
