package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ExtraTest {

    @Test
    public void testExtraCreation() {
        Extra extra = new Extra();
        extra.setId(1L);
        extra.setReservaId(100L);
        extra.setMaletaCabina(true);
        extra.setPrecioTotal(new BigDecimal("50.00"));
        
        assertNotNull(extra);
        assertEquals(1L, extra.getId());
        assertEquals(100L, extra.getReservaId());
        assertTrue(extra.getMaletaCabina());
        assertEquals(new BigDecimal("50.00"), extra.getPrecioTotal());
    }

    @Test
    public void testExtraSeguros() {
        Extra extra = new Extra();
        extra.setSeguro50(true);
        extra.setSeguro100(false);
        
        assertTrue(extra.getSeguro50());
        assertFalse(extra.getSeguro100());
    }

    @Test
    public void testExtraAllFields() {
        Extra extra = new Extra();
        extra.setId(5L);
        extra.setReservaId(200L);
        extra.setMaletaCabina(true);
        extra.setMaletaBodega(true);
        extra.setSeguro50(true);
        extra.setAsistenciaEspecial(true);
        extra.setPrecioTotal(new BigDecimal("150.00"));
        
        assertEquals(5L, extra.getId());
        assertEquals(200L, extra.getReservaId());
        assertTrue(extra.getMaletaCabina());
        assertTrue(extra.getMaletaBodega());
        assertTrue(extra.getSeguro50());
        assertTrue(extra.getAsistenciaEspecial());
        assertEquals(new BigDecimal("150.00"), extra.getPrecioTotal());
    }
}
