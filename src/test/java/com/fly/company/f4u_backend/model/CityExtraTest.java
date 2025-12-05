package com.fly.company.f4u_backend.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CityExtraTest {

    @Test
    void testCityDefaultConstructor() {
        City city = new City();
        assertNotNull(city);
        assertTrue(city.getActivo());
    }

    @Test
    void testCityParameterizedConstructor() {
        City city = new City("Bogotá", "BOG", "Colombia", "CO", "America/Bogota");
        
        assertEquals("Bogotá", city.getNombre());
        assertEquals("BOG", city.getCodigoIata());
        assertEquals("Colombia", city.getPais());
        assertEquals("CO", city.getCodigoPais());
        assertEquals("America/Bogota", city.getZonaHoraria());
        assertTrue(city.getActivo());
    }

    @Test
    void testGetSetNombre() {
        City city = new City();
        city.setNombre("Medellín");
        assertEquals("Medellín", city.getNombre());
    }

    @Test
    void testGetSetCodigoIata() {
        City city = new City();
        city.setCodigoIata("MDE");
        assertEquals("MDE", city.getCodigoIata());
    }

    @Test
    void testGetSetPais() {
        City city = new City();
        city.setPais("Colombia");
        assertEquals("Colombia", city.getPais());
    }

    @Test
    void testGetSetCodigoPais() {
        City city = new City();
        city.setCodigoPais("CO");
        assertEquals("CO", city.getCodigoPais());
    }

    @Test
    void testGetSetZonaHoraria() {
        City city = new City();
        city.setZonaHoraria("America/Bogota");
        assertEquals("America/Bogota", city.getZonaHoraria());
    }

    @Test
    void testGetSetActivo() {
        City city = new City();
        city.setActivo(false);
        assertFalse(city.getActivo());
    }

    @Test
    void testGetSetDescripcion() {
        City city = new City();
        city.setDescripcion("Ciudad capital");
        assertEquals("Ciudad capital", city.getDescripcion());
    }

    @Test
    void testGetSetFechaCreacion() {
        City city = new City();
        LocalDateTime fecha = LocalDateTime.now();
        city.setFechaCreacion(fecha);
        assertEquals(fecha, city.getFechaCreacion());
    }

    @Test
    void testGetSetFechaActualizacion() {
        City city = new City();
        LocalDateTime fecha = LocalDateTime.now();
        city.setFechaActualizacion(fecha);
        assertEquals(fecha, city.getFechaActualizacion());
    }

    @Test
    void testCityAllFields() {
        City city = new City();
        city.setId(100L);
        city.setNombre("Cali");
        city.setCodigoIata("CLO");
        city.setPais("Colombia");
        city.setCodigoPais("CO");
        city.setZonaHoraria("America/Bogota");
        city.setActivo(true);
        city.setDescripcion("Ciudad del Valle");
        LocalDateTime now = LocalDateTime.now();
        city.setFechaCreacion(now);
        city.setFechaActualizacion(now);
        
        assertEquals(100L, city.getId());
        assertEquals("Cali", city.getNombre());
        assertEquals("CLO", city.getCodigoIata());
        assertEquals("Colombia", city.getPais());
        assertEquals("CO", city.getCodigoPais());
        assertEquals("America/Bogota", city.getZonaHoraria());
        assertTrue(city.getActivo());
        assertEquals("Ciudad del Valle", city.getDescripcion());
        assertEquals(now, city.getFechaCreacion());
        assertEquals(now, city.getFechaActualizacion());
    }
}
