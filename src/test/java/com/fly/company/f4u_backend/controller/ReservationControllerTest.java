package com.fly.company.f4u_backend.controller;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.service.ReservationService;
import com.fly.company.f4u_backend.service.SeatLockService;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private SeatLockService seatLockService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private ReservationRequest testRequest;
    private Reservation testReservation;
    private Jwt mockJwt;

    @BeforeEach
    void setUp() {
        testRequest = new ReservationRequest();
        testRequest.setAsientoId(1L);
        testRequest.setLockUserId("user123");
        testRequest.setPasajeroNombre("Juan Pérez");
        testRequest.setPasajeroDocumentoNumero("1234567890");

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setCodigoReservacion("ABC123");
        testReservation.setEstado("CONFIRMADA");
        testReservation.setPasajeroNombre("Juan Pérez");

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");
        claims.put("preferred_username", "testuser");
        
        mockJwt = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claims
        );
    }

    @Test
    void testTryLock_Success() {
        when(seatLockService.tryLock(1L, "user123")).thenReturn(true);

        Map<String, String> body = Map.of("userId", "user123");
        ResponseEntity<?> response = reservationController.tryLock(1L, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatLockService).tryLock(1L, "user123");
    }

    @Test
    void testTryLock_Failed() {
        when(seatLockService.tryLock(1L, "user123")).thenReturn(false);

        Map<String, String> body = Map.of("userId", "user123");
        ResponseEntity<?> response = reservationController.tryLock(1L, body);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Seat locked", response.getBody());
        verify(seatLockService).tryLock(1L, "user123");
    }

    @Test
    void testTryLock_NoUserIdProvided() {
        when(seatLockService.tryLock(eq(1L), anyString())).thenReturn(true);

        ResponseEntity<?> response = reservationController.tryLock(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatLockService).tryLock(eq(1L), anyString());
    }

    @Test
    void testCreateReservation_Success() {
        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(testReservation);

        ResponseEntity<?> response = reservationController.createReservation(testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Reservation);
        Reservation result = (Reservation) response.getBody();
        assertEquals("ABC123", result.getCodigoReservacion());
        verify(reservationService).createReservation(testRequest);
    }

    @Test
    void testCreateReservation_IllegalArgument() {
        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid seat ID"));

        ResponseEntity<?> response = reservationController.createReservation(testRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid seat ID", response.getBody());
    }

    @Test
    void testCreateReservation_IllegalState() {
        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new IllegalStateException("Seat not available"));

        ResponseEntity<?> response = reservationController.createReservation(testRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Seat not available", response.getBody());
    }

    @Test
    void testCreateReservation_GenericException() {
        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = reservationController.createReservation(testRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Internal error"));
    }

    @Test
    void testConfirm() {
        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(testReservation);

        ResponseEntity<?> response = reservationController.confirm(testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationService).createReservation(testRequest);
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.getAll()).thenReturn(reservations);

        ResponseEntity<List<Reservation>> response = reservationController.all();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reservationService).getAll();
    }

    @Test
    void testGetUserReservations_Success() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationService.getReservationsByUserEmail("test@example.com"))
                .thenReturn(reservations);

        ResponseEntity<List<Reservation>> response = reservationController.getUserReservations(mockJwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reservationService).getReservationsByUserEmail("test@example.com");
    }

    @Test
    void testGetUserReservations_NoEmail() {
        Map<String, Object> claimsWithoutEmail = new HashMap<>();
        claimsWithoutEmail.put("sub", "testuser");
        Jwt jwtWithoutEmail = new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            Map.of("alg", "RS256"),
            claimsWithoutEmail
        );

        ResponseEntity<List<Reservation>> response = reservationController.getUserReservations(jwtWithoutEmail);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(reservationService, never()).getReservationsByUserEmail(anyString());
    }

    @Test
    void testGetUserReservations_Exception() {
        when(reservationService.getReservationsByUserEmail(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<Reservation>> response = reservationController.getUserReservations(mockJwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetUserActiveReservations_Success() {
        List<Reservation> activeReservations = Arrays.asList(testReservation);
        when(reservationService.getActiveReservationsByUserEmail("test@example.com"))
                .thenReturn(activeReservations);

        ResponseEntity<List<Reservation>> response = reservationController.getUserActiveReservations(mockJwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reservationService).getActiveReservationsByUserEmail("test@example.com");
    }
}
