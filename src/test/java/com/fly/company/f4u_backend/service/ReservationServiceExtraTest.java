package com.fly.company.f4u_backend.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.FlightRepository;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceExtraTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatLockService seatLockService;

    @Mock
    private EmailService emailService;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void testCreateReservationSeatNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setAsientoId(999L);
        
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservationSeatNotAvailable() {
        ReservationRequest request = new ReservationRequest();
        request.setAsientoId(100L);
        
        Seat seat = new Seat();
        seat.setId(100L);
        seat.setDisponible(false);
        
        when(seatRepository.findById(100L)).thenReturn(Optional.of(seat));
        
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservationSeatNotLocked() {
        ReservationRequest request = new ReservationRequest();
        request.setAsientoId(200L);
        
        Seat seat = new Seat();
        seat.setId(200L);
        seat.setDisponible(true);
        
        when(seatRepository.findById(200L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(200L)).thenReturn(false);
        
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservationInvalidEmail() {
        ReservationRequest request = new ReservationRequest();
        request.setAsientoId(300L);
        request.setLockUserId(null);
        request.setPasajeroEmail(null);
        
        Seat seat = new Seat();
        seat.setId(300L);
        seat.setDisponible(true);
        
        when(seatRepository.findById(300L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(300L)).thenReturn(true);
        
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testGetAll() {
        Reservation r1 = new Reservation();
        r1.setId(1L);
        Reservation r2 = new Reservation();
        r2.setId(2L);
        
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        
        List<Reservation> result = reservationService.getAll();
        
        assertEquals(2, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testGetAllWithMultipleReservations() {
        Reservation r1 = new Reservation();
        r1.setId(1L);
        r1.setCodigoReservacion("RES001");
        Reservation r2 = new Reservation();
        r2.setId(2L);
        r2.setCodigoReservacion("RES002");
        Reservation r3 = new Reservation();
        r3.setId(3L);
        r3.setCodigoReservacion("RES003");
        
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(r1, r2, r3));
        
        List<Reservation> result = reservationService.getAll();
        
        assertEquals(3, result.size());
        assertEquals("RES001", result.get(0).getCodigoReservacion());
        assertEquals("RES002", result.get(1).getCodigoReservacion());
        assertEquals("RES003", result.get(2).getCodigoReservacion());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testGetAllReturnsEmptyList() {
        when(reservationRepository.findAll()).thenReturn(Arrays.asList());
        
        List<Reservation> result = reservationService.getAll();
        
        assertTrue(result.isEmpty());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testGetUserStatsWithReservations() {
        Reservation r1 = new Reservation();
        r1.setEstado("CONFIRMADA");
        r1.setFechaReservacion(java.time.LocalDateTime.now());
        Reservation r2 = new Reservation();
        r2.setEstado("PENDIENTE");
        r2.setFechaReservacion(java.time.LocalDateTime.now());
        
        when(reservationRepository.findByPasajeroEmail("test@test.com")).thenReturn(Arrays.asList(r1, r2));
        
        java.util.Map<String, Object> stats = reservationService.getUserStats("test@test.com");
        
        assertEquals(2, stats.get("totalReservations"));
        assertEquals(1, stats.get("activeReservations"));
        assertEquals(2000L, stats.get("accumulatedMiles"));
        verify(reservationRepository, times(2)).findByPasajeroEmail("test@test.com");
    }

    @Test
    void testGetUserStatsEmpty() {
        when(reservationRepository.findByPasajeroEmail("empty@test.com")).thenReturn(Arrays.asList());
        when(reservationRepository.findAll()).thenReturn(Arrays.asList());
        
        java.util.Map<String, Object> stats = reservationService.getUserStats("empty@test.com");
        
        assertEquals(0, stats.get("totalReservations"));
        assertEquals(0, stats.get("activeReservations"));
        assertEquals(0L, stats.get("accumulatedMiles"));
    }

    @Test
    void testGetUserStatsLevelCalculation() {
        List<Reservation> reservations = new java.util.ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Reservation r = new Reservation();
            r.setEstado("CONFIRMADA");
            r.setFechaReservacion(java.time.LocalDateTime.now());
            reservations.add(r);
        }
        
        when(reservationRepository.findByPasajeroEmail("silver@test.com")).thenReturn(reservations);
        
        java.util.Map<String, Object> stats = reservationService.getUserStats("silver@test.com");
        
        assertEquals("Plata", stats.get("level"));
        assertEquals(6, stats.get("totalReservations"));
    }
}
