package com.fly.company.f4u_backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityControllerExtraTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityController cityController;

    @Test
    void testGetAllActiveCitiesSuccess() {
        City c1 = new City();
        c1.setId(1L);
        c1.setNombre("Bogotá");
        c1.setActivo(true);

        City c2 = new City();
        c2.setId(2L);
        c2.setNombre("Medellín");
        c2.setActivo(true);

        when(cityRepository.findByActivoTrue()).thenReturn(Arrays.asList(c1, c2));

        ResponseEntity<List<City>> response = cityController.getAllActiveCities();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Bogotá", response.getBody().get(0).getNombre());
    }

    @Test
    void testGetAllActiveCitiesException() {
        when(cityRepository.findByActivoTrue()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<City>> response = cityController.getAllActiveCities();

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testGetCityByIdFound() {
        City city = new City();
        city.setId(10L);
        city.setNombre("Cartagena");
        city.setCodigoIata("CTG");

        when(cityRepository.findById(10L)).thenReturn(Optional.of(city));

        ResponseEntity<City> response = cityController.getCityById(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cartagena", response.getBody().getNombre());
        assertEquals("CTG", response.getBody().getCodigoIata());
    }

    @Test
    void testGetCityByIdNotFound() {
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<City> response = cityController.getCityById(999L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetCityByIataCodeFound() {
        City city = new City();
        city.setId(20L);
        city.setNombre("Miami");
        city.setCodigoIata("MIA");

        when(cityRepository.findByCodigoIata("MIA")).thenReturn(Optional.of(city));

        ResponseEntity<City> response = cityController.getCityByIataCode("mia");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Miami", response.getBody().getNombre());
        verify(cityRepository, times(1)).findByCodigoIata("MIA");
    }

    @Test
    void testGetCityByIataCodeNotFound() {
        when(cityRepository.findByCodigoIata("XYZ")).thenReturn(Optional.empty());

        ResponseEntity<City> response = cityController.getCityByIataCode("xyz");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetCityByIataCodeUpperCase() {
        City city = new City();
        city.setCodigoIata("BOG");
        city.setNombre("Bogotá");

        when(cityRepository.findByCodigoIata("BOG")).thenReturn(Optional.of(city));

        ResponseEntity<City> response = cityController.getCityByIataCode("BOG");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Bogotá", response.getBody().getNombre());
    }

    @Test
    void testGetAllActiveCitiesEmpty() {
        when(cityRepository.findByActivoTrue()).thenReturn(Arrays.asList());

        ResponseEntity<List<City>> response = cityController.getAllActiveCities();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetAllActiveCitiesMultiple() {
        City c1 = new City();
        c1.setNombre("Barranquilla");
        c1.setCodigoIata("BAQ");
        c1.setActivo(true);

        City c2 = new City();
        c2.setNombre("Pereira");
        c2.setCodigoIata("PEI");
        c2.setActivo(true);

        City c3 = new City();
        c3.setNombre("Bucaramanga");
        c3.setCodigoIata("BGA");
        c3.setActivo(true);

        when(cityRepository.findByActivoTrue()).thenReturn(Arrays.asList(c1, c2, c3));

        ResponseEntity<List<City>> response = cityController.getAllActiveCities();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
        assertEquals("BAQ", response.getBody().get(0).getCodigoIata());
    }

    @Test
    void testGetCityByIataCodeLowerCase() {
        City city = new City();
        city.setCodigoIata("CTG");
        city.setNombre("Cartagena");

        when(cityRepository.findByCodigoIata("CTG")).thenReturn(Optional.of(city));

        ResponseEntity<City> response = cityController.getCityByIataCode("ctg");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cartagena", response.getBody().getNombre());
        verify(cityRepository, times(1)).findByCodigoIata("CTG");
    }

    @Test
    void testGetCityByIdWithAllFields() {
        City city = new City();
        city.setId(15L);
        city.setNombre("Santa Marta");
        city.setCodigoIata("SMR");
        city.setPais("Colombia");
        city.setActivo(true);

        when(cityRepository.findById(15L)).thenReturn(Optional.of(city));

        ResponseEntity<City> response = cityController.getCityById(15L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Santa Marta", response.getBody().getNombre());
        assertEquals("SMR", response.getBody().getCodigoIata());
        assertEquals("Colombia", response.getBody().getPais());
        assertTrue(response.getBody().getActivo());
    }

    @Test
    void testGetCitiesByCountrySuccess() {
        City c1 = new City();
        c1.setNombre("Bogotá");
        c1.setPais("Colombia");
        City c2 = new City();
        c2.setNombre("Medellín");
        c2.setPais("Colombia");

        when(cityRepository.findByPaisAndActivoTrue("Colombia")).thenReturn(Arrays.asList(c1, c2));

        ResponseEntity<List<City>> response = cityController.getCitiesByCountry("Colombia");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetCitiesByCountryEmpty() {
        when(cityRepository.findByPaisAndActivoTrue("Argentina")).thenReturn(Arrays.asList());

        ResponseEntity<List<City>> response = cityController.getCitiesByCountry("Argentina");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetCitiesByCountryException() {
        when(cityRepository.findByPaisAndActivoTrue("Brasil")).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<City>> response = cityController.getCitiesByCountry("Brasil");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
