package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
    }

    @Test
    void testReservationCreation() {
        assertNotNull(reservation);
    }

    @Test
    void testSetAndGetId() {
        reservation.setId(1L);
        assertEquals(1L, reservation.getId());
    }

    @Test
    void testSetAndGetCodigoReservacion() {
        reservation.setCodigoReservacion("F4U-20251107-ABC1");
        assertEquals("F4U-20251107-ABC1", reservation.getCodigoReservacion());
    }

    @Test
    void testSetAndGetPasajeroId() {
        reservation.setPasajeroId(10L);
        assertEquals(10L, reservation.getPasajeroId());
    }

    @Test
    void testSetAndGetVueloId() {
        reservation.setVueloId(100L);
        assertEquals(100L, reservation.getVueloId());
    }

    @Test
    void testSetAndGetAsientoId() {
        reservation.setAsientoId(50L);
        assertEquals(50L, reservation.getAsientoId());
    }

    @Test
    void testSetAndGetPasajeroTelefono() {
        reservation.setPasajeroTelefono("+573001234567");
        assertEquals("+573001234567", reservation.getPasajeroTelefono());
    }

    @Test
    void testSetAndGetClase() {
        reservation.setClase("EJECUTIVA");
        assertEquals("EJECUTIVA", reservation.getClase());
    }

    @Test
    void testSetAndGetPasajeroNombre() {
        reservation.setPasajeroNombre("Juan");
        assertEquals("Juan", reservation.getPasajeroNombre());
    }

    @Test
    void testSetAndGetPasajeroApellido() {
        reservation.setPasajeroApellido("Pérez");
        assertEquals("Pérez", reservation.getPasajeroApellido());
    }

    @Test
    void testSetAndGetPasajeroEmail() {
        reservation.setPasajeroEmail("juan@example.com");
        assertEquals("juan@example.com", reservation.getPasajeroEmail());
    }

    @Test
    void testSetAndGetDocumentoTipo() {
        reservation.setPasajeroDocumentoTipo("CC");
        assertEquals("CC", reservation.getPasajeroDocumentoTipo());
    }

    @Test
    void testSetAndGetDocumentoNumero() {
        reservation.setPasajeroDocumentoNumero("123456789");
        assertEquals("123456789", reservation.getPasajeroDocumentoNumero());
    }

    @Test
    void testSetAndGetFechaNacimiento() {
        LocalDate fecha = LocalDate.of(1990, 5, 15);
        reservation.setPasajeroFechaNacimiento(fecha);
        assertEquals(fecha, reservation.getPasajeroFechaNacimiento());
    }

    @Test
    void testSetAndGetPrecioAsiento() {
        BigDecimal precio = new BigDecimal("350000");
        reservation.setPrecioAsiento(precio);
        assertEquals(precio, reservation.getPrecioAsiento());
    }

    @Test
    void testSetAndGetPrecioExtras() {
        BigDecimal precio = new BigDecimal("120000");
        reservation.setPrecioExtras(precio);
        assertEquals(precio, reservation.getPrecioExtras());
    }

    @Test
    void testSetAndGetPrecioTotal() {
        BigDecimal precio = new BigDecimal("470000");
        reservation.setPrecioTotal(precio);
        assertEquals(precio, reservation.getPrecioTotal());
    }

    @Test
    void testSetAndGetExtraFlags() {
        reservation.setExtraMaletaCabina(true);
        reservation.setExtraMaletaBodega(false);
        reservation.setExtraSeguro50(true);
        reservation.setExtraSeguro100(false);
        reservation.setExtraAsistenciaEspecial(true);

        assertTrue(reservation.getExtraMaletaCabina());
        assertFalse(reservation.getExtraMaletaBodega());
        assertTrue(reservation.getExtraSeguro50());
        assertFalse(reservation.getExtraSeguro100());
        assertTrue(reservation.getExtraAsistenciaEspecial());
    }

    @Test
    void testSetAndGetMetodoPago() {
        reservation.setMetodoPago("TARJETA_CREDITO");
        assertEquals("TARJETA_CREDITO", reservation.getMetodoPago());
    }

    @Test
    void testSetAndGetEstado() {
        reservation.setEstado("CONFIRMADA");
        assertEquals("CONFIRMADA", reservation.getEstado());
    }

    @Test
    void testSetAndGetEstadoPago() {
        reservation.setEstadoPago("PAGADO");
        assertEquals("PAGADO", reservation.getEstadoPago());
    }

    @Test
    void testSetAndGetFechaCreacion() {
        LocalDateTime now = LocalDateTime.now();
        reservation.setFechaCreacion(now);
        assertEquals(now, reservation.getFechaCreacion());
    }

    @Test
    void testSetAndGetFechaActualizacion() {
        LocalDateTime now = LocalDateTime.now();
        reservation.setFechaActualizacion(now);
        assertEquals(now, reservation.getFechaActualizacion());
    }

    @Test
    void testPriceCalculations() {
        BigDecimal asiento = new BigDecimal("350000");
        BigDecimal extras = new BigDecimal("120000");
        BigDecimal total = asiento.add(extras);

        reservation.setPrecioAsiento(asiento);
        reservation.setPrecioExtras(extras);
        reservation.setPrecioTotal(total);

        assertEquals(new BigDecimal("470000"), reservation.getPrecioTotal());
    }

    @Test
    void testEstadosReservacion() {
        String[] estados = {"PENDIENTE_PAGO", "CONFIRMADA", "CHECK_IN_COMPLETADO", "EN_VUELO", "COMPLETADA", "CANCELADA"};
        
        for (String estado : estados) {
            reservation.setEstado(estado);
            assertEquals(estado, reservation.getEstado());
        }
    }
}
