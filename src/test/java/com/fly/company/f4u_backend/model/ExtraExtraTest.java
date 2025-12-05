package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ExtraExtraTest {

    @Test
    void testExtraDefaultConstructor() {
        Extra extra = new Extra();
        assertNotNull(extra);
        assertFalse(extra.getMaletaCabina());
        assertFalse(extra.getMaletaBodega());
        assertFalse(extra.getSeguro50());
        assertFalse(extra.getSeguro100());
        assertFalse(extra.getAsistenciaEspecial());
    }

    @Test
    void testGetSetReservaId() {
        Extra extra = new Extra();
        extra.setReservaId(100L);
        assertEquals(100L, extra.getReservaId());
    }

    @Test
    void testGetSetMaletaCabina() {
        Extra extra = new Extra();
        extra.setMaletaCabina(true);
        assertTrue(extra.getMaletaCabina());
    }

    @Test
    void testGetSetMaletaBodega() {
        Extra extra = new Extra();
        extra.setMaletaBodega(true);
        assertTrue(extra.getMaletaBodega());
    }

    @Test
    void testGetSetSeguro50() {
        Extra extra = new Extra();
        extra.setSeguro50(true);
        assertTrue(extra.getSeguro50());
    }

    @Test
    void testGetSetSeguro100() {
        Extra extra = new Extra();
        extra.setSeguro100(true);
        assertTrue(extra.getSeguro100());
    }

    @Test
    void testGetSetAsistenciaEspecial() {
        Extra extra = new Extra();
        extra.setAsistenciaEspecial(true);
        assertTrue(extra.getAsistenciaEspecial());
    }

    @Test
    void testGetSetPrecioTotal() {
        Extra extra = new Extra();
        BigDecimal precio = new BigDecimal("75.50");
        extra.setPrecioTotal(precio);
        assertEquals(precio, extra.getPrecioTotal());
    }

    @Test
    void testGetSetFechaCreacion() {
        Extra extra = new Extra();
        LocalDateTime fecha = LocalDateTime.now();
        extra.setFechaCreacion(fecha);
        assertEquals(fecha, extra.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        Extra extra = new Extra();
        LocalDateTime fecha = LocalDateTime.now();
        extra.setFechaActualizacion(fecha);
        assertEquals(fecha, extra.getFechaActualizacion());
    }

    @Test
    void testExtraAllFieldsEnabled() {
        Extra extra = new Extra();
        extra.setId(50L);
        extra.setReservaId(200L);
        extra.setMaletaCabina(true);
        extra.setMaletaBodega(true);
        extra.setSeguro50(true);
        extra.setSeguro100(false);
        extra.setAsistenciaEspecial(true);
        extra.setPrecioTotal(new BigDecimal("150.00"));
        
        assertEquals(50L, extra.getId());
        assertEquals(200L, extra.getReservaId());
        assertTrue(extra.getMaletaCabina());
        assertTrue(extra.getMaletaBodega());
        assertTrue(extra.getSeguro50());
        assertFalse(extra.getSeguro100());
        assertTrue(extra.getAsistenciaEspecial());
        assertEquals(new BigDecimal("150.00"), extra.getPrecioTotal());
    }

    @Test
    void testExtraAllFieldsDisabled() {
        Extra extra = new Extra();
        extra.setMaletaCabina(false);
        extra.setMaletaBodega(false);
        extra.setSeguro50(false);
        extra.setSeguro100(false);
        extra.setAsistenciaEspecial(false);
        
        assertFalse(extra.getMaletaCabina());
        assertFalse(extra.getMaletaBodega());
        assertFalse(extra.getSeguro50());
        assertFalse(extra.getSeguro100());
        assertFalse(extra.getAsistenciaEspecial());
    }
}
