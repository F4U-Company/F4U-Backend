package com.fly.company.f4u_backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.repository.FlightRepository;

@ExtendWith(MockitoExtension.class)
class FlightControllerExtraTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightController flightController;

    @Test
    void testSearchFlightsSuccess() {
        City origin = new City();
        origin.setId(1L);
        City destination = new City();
        destination.setId(2L);

        Flight f1 = new Flight();
        f1.setId(100L);
        f1.setNumeroVuelo("AA123");
        f1.setCiudadOrigen(origin);
        f1.setCiudadDestino(destination);

        when(flightRepository.findFlightsByOriginDestinationAndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(f1));

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, LocalDate.of(2025, 10, 25));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("AA123", response.getBody().get(0).getNumeroVuelo());
    }

    @Test
    void testSearchFlightsEmpty() {
        when(flightRepository.findFlightsByOriginDestinationAndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList());

        ResponseEntity<List<Flight>> response = flightController.searchFlights(5L, 6L, LocalDate.of(2025, 12, 1));

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearchFlightsException() {
        when(flightRepository.findFlightsByOriginDestinationAndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, LocalDate.now());

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testGetAllFlights() {
        Flight f1 = new Flight();
        f1.setId(1L);
        Flight f2 = new Flight();
        f2.setId(2L);

        when(flightRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        ResponseEntity<List<Flight>> response = flightController.all();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateFlight() {
        Flight newFlight = new Flight();
        newFlight.setNumeroVuelo("BB456");

        when(flightRepository.save(any(Flight.class))).thenReturn(newFlight);

        ResponseEntity<Flight> response = flightController.create(newFlight);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("BB456", response.getBody().getNumeroVuelo());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testGetFlightByIdFound() {
        City origin = new City();
        origin.setNombre("Bogotá");
        City destination = new City();
        destination.setNombre("Miami");

        Flight flight = new Flight();
        flight.setId(50L);
        flight.setNumeroVuelo("CC789");
        flight.setCiudadOrigen(origin);
        flight.setCiudadDestino(destination);

        when(flightRepository.findById(50L)).thenReturn(Optional.of(flight));

        ResponseEntity<Flight> response = flightController.getFlightById(50L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CC789", response.getBody().getNumeroVuelo());
    }

    @Test
    void testGetFlightByIdNotFound() {
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Flight> response = flightController.getFlightById(999L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetFlightByIdException() {
        when(flightRepository.findById(anyLong())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Flight> response = flightController.getFlightById(100L);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testDeleteFlight() {
        when(flightRepository.existsById(10L)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(10L);

        ResponseEntity<?> response = flightController.delete(10L);

        assertEquals(204, response.getStatusCodeValue());
        verify(flightRepository, times(1)).deleteById(10L);
    }

    @Test
    void testGetAllFlightsEmpty() {
        when(flightRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<List<Flight>> response = flightController.all();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCreateFlightWithDetails() {
        City origin = new City();
        origin.setId(1L);
        City destination = new City();
        destination.setId(2L);

        Flight newFlight = new Flight();
        newFlight.setNumeroVuelo("XY999");
        newFlight.setCiudadOrigen(origin);
        newFlight.setCiudadDestino(destination);

        when(flightRepository.save(any(Flight.class))).thenReturn(newFlight);

        ResponseEntity<Flight> response = flightController.create(newFlight);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("XY999", response.getBody().getNumeroVuelo());
    }

    @Test
    void testSearchFlightsMultipleResults() {
        Flight f1 = new Flight();
        f1.setNumeroVuelo("FL001");
        Flight f2 = new Flight();
        f2.setNumeroVuelo("FL002");
        Flight f3 = new Flight();
        f3.setNumeroVuelo("FL003");

        when(flightRepository.findFlightsByOriginDestinationAndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(f1, f2, f3));

        ResponseEntity<List<Flight>> response = flightController.searchFlights(1L, 2L, LocalDate.of(2025, 12, 10));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
    }

    @Test
    void testDeleteFlightNotFound() {
        when(flightRepository.existsById(999L)).thenReturn(false);

        ResponseEntity<?> response = flightController.delete(999L);

        assertEquals(404, response.getStatusCodeValue());
        verify(flightRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testGetFlightByIdWithCityDetails() {
        City origin = new City();
        origin.setId(1L);
        origin.setNombre("Medellín");
        origin.setCodigoIata("MDE");

        City destination = new City();
        destination.setId(2L);
        destination.setNombre("Cali");
        destination.setCodigoIata("CLO");

        Flight flight = new Flight();
        flight.setId(75L);
        flight.setNumeroVuelo("AV123");
        flight.setCiudadOrigen(origin);
        flight.setCiudadDestino(destination);

        when(flightRepository.findById(75L)).thenReturn(Optional.of(flight));

        ResponseEntity<Flight> response = flightController.getFlightById(75L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("AV123", response.getBody().getNumeroVuelo());
        assertEquals("Medellín", response.getBody().getCiudadOrigen().getNombre());
        assertEquals("Cali", response.getBody().getCiudadDestino().getNombre());
    }
}
