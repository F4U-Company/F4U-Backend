package com.fly.company.f4u_backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.service.ReservationService;
import com.fly.company.f4u_backend.service.SeatLockService;

@ExtendWith(MockitoExtension.class)
class ReservationControllerExtraTest {

    @Mock
    private SeatLockService seatLockService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @Test
    void testTryLockSuccess() {
        when(seatLockService.tryLock(eq(100L), anyString())).thenReturn(true);
        
        Map<String, String> body = Map.of("userId", "user123");
        ResponseEntity<?> response = reservationController.tryLock(100L, body);
        
        assertEquals(200, response.getStatusCodeValue());
        verify(seatLockService, times(1)).tryLock(eq(100L), anyString());
    }

    @Test
    void testTryLockFailed() {
        when(seatLockService.tryLock(eq(200L), anyString())).thenReturn(false);
        
        Map<String, String> body = Map.of("userId", "user456");
        ResponseEntity<?> response = reservationController.tryLock(200L, body);
        
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Seat locked", response.getBody());
    }

    @Test
    void testTryLockWithNullUserId() {
        when(seatLockService.tryLock(eq(300L), anyString())).thenReturn(true);
        
        ResponseEntity<?> response = reservationController.tryLock(300L, null);
        
        assertEquals(200, response.getStatusCodeValue());
        verify(seatLockService, times(1)).tryLock(eq(300L), startsWith("reservation-user-"));
    }

    @Test
    void testTryLockWithEmptyUserId() {
        when(seatLockService.tryLock(eq(400L), anyString())).thenReturn(true);
        
        Map<String, String> body = Map.of("userId", "");
        ResponseEntity<?> response = reservationController.tryLock(400L, body);
        
        assertEquals(200, response.getStatusCodeValue());
        verify(seatLockService, times(1)).tryLock(eq(400L), startsWith("reservation-user-"));
    }

    @Test
    void testCreateReservationSuccess() {
        ReservationRequest request = new ReservationRequest();
        request.setVueloId(100L);
        request.setAsientoId(50L);
        request.setPasajeroNombre("Juan");
        request.setPasajeroApellido("Pérez");
        request.setPasajeroEmail("juan@test.com");
        
        Reservation reservation = new Reservation();
        reservation.setId(1000L);
        reservation.setCodigoReservacion("RES123");
        
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenReturn(reservation);
        
        ResponseEntity<?> response = reservationController.createReservation(request);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(reservationService, times(1)).createReservation(any(ReservationRequest.class));
    }

    @Test
    void testCreateReservationIllegalArgument() {
        ReservationRequest request = new ReservationRequest();
        
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenThrow(new IllegalArgumentException("Seat not found"));
        
        ResponseEntity<?> response = reservationController.createReservation(request);
        
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Seat not found", response.getBody());
    }

    @Test
    void testCreateReservationIllegalState() {
        ReservationRequest request = new ReservationRequest();
        
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenThrow(new IllegalStateException("Seat already assigned"));
        
        ResponseEntity<?> response = reservationController.createReservation(request);
        
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Seat already assigned", response.getBody());
    }

    @Test
    void testCreateReservationInternalError() {
        ReservationRequest request = new ReservationRequest();
        
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        ResponseEntity<?> response = reservationController.createReservation(request);
        
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Internal error"));
    }

    @Test
    void testConfirmReservation() {
        ReservationRequest request = new ReservationRequest();
        Reservation reservation = new Reservation();
        reservation.setId(2000L);
        
        when(reservationService.createReservation(any(ReservationRequest.class)))
            .thenReturn(reservation);
        
        ResponseEntity<?> response = reservationController.confirm(request);
        
        assertEquals(200, response.getStatusCodeValue());
        verify(reservationService, times(1)).createReservation(any(ReservationRequest.class));
    }

    @Test
    void testGetAllReservations() {
        Reservation r1 = new Reservation();
        r1.setId(1L);
        Reservation r2 = new Reservation();
        r2.setId(2L);
        List<Reservation> reservations = Arrays.asList(r1, r2);
        
        when(reservationService.getAll()).thenReturn(reservations);
        
        ResponseEntity<List<Reservation>> response = reservationController.all();
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(reservationService, times(1)).getAll();
    }

    @Test
    void testGetAllReservationsEmpty() {
        when(reservationService.getAll()).thenReturn(Arrays.asList());
        
        ResponseEntity<List<Reservation>> response = reservationController.all();
        
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }
}
