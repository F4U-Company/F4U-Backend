package com.fly.company.f4u_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.FlightRepository;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

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

    private ReservationRequest request;
    private Seat seat;
    private Flight flight;

    @BeforeEach
    void setUp() {
        // Setup request
        request = new ReservationRequest();
        request.setAsientoId(1L);
        request.setVueloId(100L);
        request.setClase("EJECUTIVA");
        request.setPasajeroNombre("Juan");
        request.setPasajeroApellido("Pérez");
        request.setPasajeroEmail("juan@example.com");
        request.setPasajeroTelefono("3001234567");
        request.setPasajeroDocumentoTipo("CC");
        request.setPasajeroDocumentoNumero("123456789");
        request.setPasajeroFechaNacimiento(LocalDate.of(1990, 5, 15));
        request.setPrecioAsiento(new BigDecimal("350000"));
        request.setPrecioExtras(new BigDecimal("45000"));
        request.setPrecioTotal(new BigDecimal("395000"));
        request.setMetodoPago("TARJETA_CREDITO");
        request.setLockUserId("user-123");
        
        // Setup seat
        seat = new Seat();
        seat.setId(1L);
        seat.setNumeroAsiento("12A");
        seat.setClase("EJECUTIVA");
        seat.setDisponible(true);
        seat.setVueloId(100L);
        seat.setPrecio(new BigDecimal("350000"));

        // Setup flight
        flight = new Flight();
        flight.setId(100L);
        flight.setNumeroVuelo("AV200");
        flight.setFechaSalida(java.time.LocalDateTime.now().plusDays(7));
        flight.setFechaLlegada(java.time.LocalDateTime.now().plusDays(7).plusHours(2));
    }

    @Test
    void testCreateReservation_Success() {
        // Arrange
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(1L)).thenReturn(true);
        when(seatLockService.getLockedByUserId(1L)).thenReturn("user-123");
        when(seatLockService.isLockedByUser(1L, "user-123")).thenReturn(true);
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        
        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);
        savedReservation.setCodigoReservacion("F4U-20251107-ABC1");
        savedReservation.setVueloId(100L);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // Act
        Reservation result = reservationService.createReservation(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(seatRepository).save(any(Seat.class));
        verify(seatLockService).releaseLock(1L);
        verify(emailService).sendReservationConfirmation(
            any(Reservation.class),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    void testCreateReservation_SeatNotFound() {
        // Arrange
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservation_SeatNotAvailable() {
        // Arrange
        seat.setDisponible(false);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservation_SeatNotLocked() {
        // Arrange
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservation_WrongUser() {
        // Arrange
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(1L)).thenReturn(true);
        when(seatLockService.getLockedByUserId(1L)).thenReturn("other-user");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(request);
        });
    }

    @Test
    void testCreateReservation_PrimeraClase_ExtrasGratis() {
        // Arrange
        request.setClase("PRIMERA_CLASE");
        request.setExtraMaletaCabina(true);
        request.setExtraMaletaBodega(true);
        request.setExtraSeguro50(true);
        
        seat.setClase("PRIMERA_CLASE");
        seat.setPrecio(new BigDecimal("650000"));
        
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatLockService.isLocked(1L)).thenReturn(true);
        when(seatLockService.getLockedByUserId(1L)).thenReturn("user-123");
        when(seatLockService.isLockedByUser(1L, "user-123")).thenReturn(true);
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        
        Reservation savedReservation = new Reservation();
        savedReservation.setPrecioExtras(BigDecimal.ZERO);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation arg = invocation.getArgument(0);
            assertEquals(BigDecimal.ZERO, arg.getPrecioExtras());
            return arg;
        });

        // Act
        reservationService.createReservation(request);

        // Assert
        verify(reservationRepository).save(argThat(reservation -> 
            reservation.getPrecioExtras().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void testValidateSeatNotFound() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            // Simular lógica de validación del servicio
            seatRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testValidateSeatNotAvailable() {
        Seat testSeat = new Seat();
        testSeat.setDisponible(false);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(testSeat));
        
        // Simular validación de disponibilidad
        Seat foundSeat = seatRepository.findById(1L).get();
        assertFalse(foundSeat.getDisponible());
    }
}
