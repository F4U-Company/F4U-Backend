package com.fly.company.f4u_backend.controller;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Deshabilita filtros de seguridad para tests
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityRepository cityRepository;

    private City city1;
    private City city2;

    @BeforeEach
    void setUp() {
        city1 = new City();
        city1.setId(1L);
        city1.setNombre("Bogotá");
        city1.setCodigoIata("BOG");
        city1.setPais("Colombia");
        city1.setActivo(true);

        city2 = new City();
        city2.setId(2L);
        city2.setNombre("Medellín");
        city2.setCodigoIata("MDE");
        city2.setPais("Colombia");
        city2.setActivo(true);
    }

    @Test
    void testGetAllCities() throws Exception {
        // Arrange
        List<City> cities = Arrays.asList(city1, city2);
        when(cityRepository.findByActivoTrue()).thenReturn(cities);

        // Act & Assert
        mockMvc.perform(get("/api/cities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Bogotá"))
                .andExpect(jsonPath("$[1].nombre").value("Medellín"));

        verify(cityRepository).findByActivoTrue();
    }

    @Test
    void testGetAllCities_Empty() throws Exception {
        // Arrange
        when(cityRepository.findByActivoTrue()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/cities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetCityById_Found() throws Exception {
        when(cityRepository.findById(1L)).thenReturn(java.util.Optional.of(city1));

        mockMvc.perform(get("/api/cities/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Bogotá"));
    }

    @Test
    void testGetCityById_NotFound() throws Exception {
        when(cityRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/cities/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCityByIataCode_Found() throws Exception {
        when(cityRepository.findByCodigoIata("BOG")).thenReturn(java.util.Optional.of(city1));

        mockMvc.perform(get("/api/cities/iata/BOG")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoIata").value("BOG"));
    }

    @Test
    void testGetCityByIataCode_NotFound() throws Exception {
        when(cityRepository.findByCodigoIata("XYZ")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/cities/iata/XYZ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
