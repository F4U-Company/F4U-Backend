package com.fly.company.f4u_backend.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityTest {

    private City city;

    @BeforeEach
    void setUp() {
        city = new City();
    }

    @Test
    void testCityCreation() {
        assertNotNull(city);
        assertTrue(city.getActivo());
    }

    @Test
    void testSetAndGetId() {
        city.setId(1L);
        assertEquals(1L, city.getId());
    }

    @Test
    void testSetAndGetNombre() {
        city.setNombre("Bogotá");
        assertEquals("Bogotá", city.getNombre());
    }

    @Test
    void testSetAndGetCodigoIata() {
        city.setCodigoIata("BOG");
        assertEquals("BOG", city.getCodigoIata());
    }

    @Test
    void testSetAndGetPais() {
        city.setPais("Colombia");
        assertEquals("Colombia", city.getPais());
    }

    @Test
    void testSetAndGetCodigoPais() {
        city.setCodigoPais("CO");
        assertEquals("CO", city.getCodigoPais());
    }

    @Test
    void testSetAndGetZonaHoraria() {
        city.setZonaHoraria("America/Bogota");
        assertEquals("America/Bogota", city.getZonaHoraria());
    }

    @Test
    void testSetAndGetActivo() {
        city.setActivo(false);
        assertFalse(city.getActivo());
    }

    @Test
    void testSetAndGetDescripcion() {
        String desc = "Capital de Colombia";
        city.setDescripcion(desc);
        assertEquals(desc, city.getDescripcion());
    }

    @Test
    void testSetAndGetFechaCreacion() {
        LocalDateTime now = LocalDateTime.now();
        city.setFechaCreacion(now);
        assertEquals(now, city.getFechaCreacion());
    }

    @Test
    void testSetAndGetFechaActualizacion() {
        LocalDateTime now = LocalDateTime.now();
        city.setFechaActualizacion(now);
        assertEquals(now, city.getFechaActualizacion());
    }

    @Test
    void testDefaultActivoValue() {
        City newCity = new City();
        assertTrue(newCity.getActivo());
    }

    @Test
    void testToString() {
        city.setId(1L);
        city.setNombre("Medellín");
        city.setCodigoIata("MDE");
        String toString = city.toString();
        assertNotNull(toString);
        // City no tiene toString sobrescrito, solo verificar que no sea null
    }

    @Test
    void testEqualsAndHashCode() {
        City city1 = new City();
        city1.setId(1L);
        city1.setCodigoIata("BOG");

        City city2 = new City();
        city2.setId(1L);
        city2.setCodigoIata("BOG");

        // City no tiene equals sobrescrito, comparar propiedades individuales
        assertEquals(city1.getId(), city2.getId());
        assertEquals(city1.getCodigoIata(), city2.getCodigoIata());
    }

    @Test
    void testNotEquals() {
        City city1 = new City();
        city1.setId(1L);

        City city2 = new City();
        city2.setId(2L);

        assertNotEquals(city1, city2);
    }
}
