package com.fly.company.f4u_backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightController flightController;

    private Flight testFlight;
    private City origen;
    private City destino;

    @BeforeEach
    void setUp() {
        origen = new City();
        origen.setId(1L);
        origen.setNombre("Bogotá");
        origen.setCodigoIata("BOG");

        destino = new City();
        destino.setId(2L);
        destino.setNombre("Medellín");
        destino.setCodigoIata("MDE");

        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setNumeroVuelo("AV123");
        testFlight.setCiudadOrigen(origen);
        testFlight.setCiudadDestino(destino);
        testFlight.setFechaSalida(LocalDateTime.of(2025, 10, 25, 10, 0));
        testFlight.setFechaLlegada(LocalDateTime.of(2025, 10, 25, 12, 0));
    }

    @Test
    void testSearchFlights_Success() {
        LocalDate searchDate = LocalDate.of(2025, 10, 25);
        LocalDateTime dateTime = searchDate.atStartOfDay();
        List<Flight> expectedFlights = Arrays.asList(testFlight);

        when(flightRepository.findFlightsByOriginDestinationAndDate(1L, 2L, dateTime))
                .thenReturn(expectedFlights);

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, searchDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("AV123", response.getBody().get(0).getNumeroVuelo());
        verify(flightRepository).findFlightsByOriginDestinationAndDate(1L, 2L, dateTime);
    }

    @Test
    void testSearchFlights_NoResults() {
        LocalDate searchDate = LocalDate.of(2025, 10, 25);
        LocalDateTime dateTime = searchDate.atStartOfDay();

        when(flightRepository.findFlightsByOriginDestinationAndDate(1L, 2L, dateTime))
                .thenReturn(Arrays.asList());

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, searchDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearchFlights_ExceptionHandling() {
        LocalDate searchDate = LocalDate.of(2025, 10, 25);
        LocalDateTime dateTime = searchDate.atStartOfDay();

        when(flightRepository.findFlightsByOriginDestinationAndDate(1L, 2L, dateTime))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, searchDate);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetAllFlights() {
        List<Flight> expectedFlights = Arrays.asList(testFlight);
        when(flightRepository.findAll()).thenReturn(expectedFlights);

        ResponseEntity<List<Flight>> response = flightController.all();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(flightRepository).findAll();
    }

    @Test
    void testCreateFlight() {
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        ResponseEntity<Flight> response = flightController.create(testFlight);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AV123", response.getBody().getNumeroVuelo());
        verify(flightRepository).save(testFlight);
    }

    @Test
    void testGetFlightById_Found() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

        ResponseEntity<Flight> response = flightController.getFlightById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AV123", response.getBody().getNumeroVuelo());
        verify(flightRepository).findById(1L);
    }

    @Test
    void testGetFlightById_NotFound() {
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Flight> response = flightController.getFlightById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(flightRepository).findById(999L);
    }

    @Test
    void testGetFlightById_ExceptionHandling() {
        when(flightRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Flight> response = flightController.getFlightById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}


