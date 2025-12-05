package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReservationExtraFieldsTest {

    @Test
    void testReservationCreation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCodigoReservacion("ABC123");
        
        assertNotNull(reservation);
        assertEquals(1L, reservation.getId());
        assertEquals("ABC123", reservation.getCodigoReservacion());
    }

    @Test
    void testReservationPassengerInfo() {
        Reservation reservation = new Reservation();
        reservation.setPasajeroNombre("Juan");
        reservation.setPasajeroEmail("juan@example.com");
        
        assertEquals("Juan", reservation.getPasajeroNombre());
        assertEquals("juan@example.com", reservation.getPasajeroEmail());
    }

    @Test
    void testReservationPrices() {
        Reservation reservation = new Reservation();
        reservation.setPrecioAsiento(new BigDecimal("150000"));
        reservation.setPrecioExtras(new BigDecimal("45000"));
        reservation.setPrecioTotal(new BigDecimal("195000"));
        
        assertEquals(new BigDecimal("150000"), reservation.getPrecioAsiento());
        assertEquals(new BigDecimal("45000"), reservation.getPrecioExtras());
        assertEquals(new BigDecimal("195000"), reservation.getPrecioTotal());
    }

    @Test
    void testReservationFlightAndSeat() {
        Reservation reservation = new Reservation();
        reservation.setVueloId(100L);
        reservation.setAsientoId(1L);
        
        assertEquals(100L, reservation.getVueloId());
        assertEquals(1L, reservation.getAsientoId());
    }

    @Test
    void testReservationDocument() {
        Reservation reservation = new Reservation();
        reservation.setPasajeroDocumentoTipo("CC");
        reservation.setPasajeroDocumentoNumero("123456789");
        
        assertEquals("CC", reservation.getPasajeroDocumentoTipo());
        assertEquals("123456789", reservation.getPasajeroDocumentoNumero());
    }

    @Test
    void testReservationBirthDate() {
        Reservation reservation = new Reservation();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        reservation.setPasajeroFechaNacimiento(birthDate);
        
        assertEquals(birthDate, reservation.getPasajeroFechaNacimiento());
    }

    @Test
    void testReservationPaymentMethod() {
        Reservation reservation = new Reservation();
        reservation.setMetodoPago("TARJETA_CREDITO");
        
        assertEquals("TARJETA_CREDITO", reservation.getMetodoPago());
    }

    @Test
    void testReservationExtras() {
        Reservation reservation = new Reservation();
        reservation.setExtraMaletaCabina(true);
        reservation.setExtraMaletaBodega(false);
        reservation.setExtraSeguro50(true);
        
        assertTrue(reservation.getExtraMaletaCabina());
        assertFalse(reservation.getExtraMaletaBodega());
        assertTrue(reservation.getExtraSeguro50());
    }

    @Test
    void testReservationAllFields() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCodigoReservacion("F4U-123");
        reservation.setVueloId(100L);
        reservation.setAsientoId(1L);
        reservation.setPasajeroNombre("Juan");
        reservation.setPasajeroEmail("juan@test.com");
        reservation.setPrecioTotal(new BigDecimal("200000"));
        
        assertNotNull(reservation.getId());
        assertNotNull(reservation.getCodigoReservacion());
        assertNotNull(reservation.getVueloId());
        assertNotNull(reservation.getAsientoId());
        assertNotNull(reservation.getPasajeroNombre());
        assertNotNull(reservation.getPasajeroEmail());
        assertNotNull(reservation.getPrecioTotal());
    }
}
