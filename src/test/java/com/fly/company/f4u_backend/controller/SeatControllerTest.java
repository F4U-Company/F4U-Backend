package com.fly.company.f4u_backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.model.SeatWithLockInfo;
import com.fly.company.f4u_backend.repository.SeatRepository;
import com.fly.company.f4u_backend.service.SeatLockService;

@ExtendWith(MockitoExtension.class)
class SeatControllerTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatLockService seatLockService;

    @InjectMocks
    private SeatController seatController;

    private Seat testSeat;

    @BeforeEach
    void setUp() {
        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setFila(1);
        testSeat.setColumna("A");
        testSeat.setDisponible(true);
        testSeat.setClase("economica");
    }

    @Test
    void testGetSeatsByFlight_WithLocks() {
        Long flightId = 1L;
        List<Seat> seats = Arrays.asList(testSeat);

        when(seatRepository.findByVueloId(flightId)).thenReturn(seats);
        when(seatLockService.isLocked(1L)).thenReturn(true);
        when(seatLockService.getRemainingLockTimeSeconds(1L)).thenReturn(120L);
        when(seatLockService.getLockedByUserId(1L)).thenReturn("user123");

        ResponseEntity<List<SeatWithLockInfo>> response = seatController.getSeatsByFlight(flightId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        SeatWithLockInfo seatWithLock = response.getBody().get(0);
        assertTrue(seatWithLock.isLocked());
        assertEquals(120L, seatWithLock.getRemainingLockSeconds());
        assertEquals("user123", seatWithLock.getLockedByUserId());
        
        verify(seatRepository).findByVueloId(flightId);
        verify(seatLockService).isLocked(1L);
        verify(seatLockService).getRemainingLockTimeSeconds(1L);
        verify(seatLockService).getLockedByUserId(1L);
    }

    @Test
    void testGetSeatsByFlight_NoLocks() {
        Long flightId = 1L;
        List<Seat> seats = Arrays.asList(testSeat);

        when(seatRepository.findByVueloId(flightId)).thenReturn(seats);
        when(seatLockService.isLocked(1L)).thenReturn(false);
        when(seatLockService.getRemainingLockTimeSeconds(1L)).thenReturn(0L);
        when(seatLockService.getLockedByUserId(1L)).thenReturn(null);

        ResponseEntity<List<SeatWithLockInfo>> response = seatController.getSeatsByFlight(flightId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        SeatWithLockInfo seatWithLock = response.getBody().get(0);
        assertFalse(seatWithLock.isLocked());
        assertEquals(0L, seatWithLock.getRemainingLockSeconds());
        assertNull(seatWithLock.getLockedByUserId());
    }

    @Test
    void testReserveSeat_Success() {
        testSeat.setDisponible(true);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));
        when(seatRepository.save(any(Seat.class))).thenReturn(testSeat);

        ResponseEntity<Seat> response = seatController.reserveSeat(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getDisponible());
        verify(seatRepository).findById(1L);
        verify(seatRepository).save(testSeat);
    }

    @Test
    void testReserveSeat_AlreadyReserved() {
        testSeat.setDisponible(false);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));

        ResponseEntity<Seat> response = seatController.reserveSeat(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(seatRepository).findById(1L);
        verify(seatRepository, never()).save(any(Seat.class));
    }

    @Test
    void testReserveSeat_NotFound() {
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Seat> response = seatController.reserveSeat(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(seatRepository).findById(999L);
    }

    @Test
    void testReleaseSeat_Success() {
        testSeat.setDisponible(false);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));
        when(seatRepository.save(any(Seat.class))).thenReturn(testSeat);

        ResponseEntity<Seat> response = seatController.releaseSeat(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getDisponible());
        verify(seatRepository).findById(1L);
        verify(seatRepository).save(testSeat);
    }

    @Test
    void testReleaseSeat_NotFound() {
        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Seat> response = seatController.releaseSeat(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(seatRepository).findById(999L);
    }
}
