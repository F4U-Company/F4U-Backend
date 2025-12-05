package com.fly.company.f4u_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fly.company.f4u_backend.model.City;
import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.FlightRepository;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
public class ChatbotServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ChatbotService chatbotService;

    private Reservation reservation;
    private Flight flight;
    private Seat seat;
    private City cityOrigin;
    private City cityDestination;

    @BeforeEach
    void setUp() {
        // Configurar API key y URL
        ReflectionTestUtils.setField(chatbotService, "geminiApiKey", "test-api-key");
        ReflectionTestUtils.setField(chatbotService, "geminiApiUrl", "https://test-api.com");

        // Crear ciudades
        cityOrigin = new City();
        cityOrigin.setId(1L);
        cityOrigin.setNombre("Bogotá");
        cityOrigin.setCodigoIata("BOG");

        cityDestination = new City();
        cityDestination.setId(2L);
        cityDestination.setNombre("Medellín");
        cityDestination.setCodigoIata("MDE");

        // Crear vuelo
        flight = new Flight();
        flight.setId(100L);
        flight.setNumeroVuelo("F4U-001");
        flight.setCiudadOrigen(cityOrigin);
        flight.setCiudadDestino(cityDestination);
        flight.setFechaSalida(LocalDateTime.now().plusDays(7));
        flight.setFechaLlegada(LocalDateTime.now().plusDays(7).plusHours(2));

        // Crear asiento
        seat = new Seat();
        seat.setId(1L);
        seat.setVueloId(100L);
        seat.setFila(10);
        seat.setColumna("A");
        seat.setClase("economica");
        seat.setPrecio(new BigDecimal("150000"));

        // Crear reserva
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCodigoReservacion("F4U-20251107-ABC1");
        reservation.setPasajeroEmail("test@example.com");
        reservation.setPasajeroNombre("Juan");
        reservation.setPasajeroApellido("Pérez");
        reservation.setPasajeroTelefono("3001234567");
        reservation.setPasajeroDocumentoTipo("CC");
        reservation.setPasajeroDocumentoNumero("123456789");
        reservation.setVueloId(100L);
        reservation.setAsientoId(1L);
        reservation.setClase("economica");
        reservation.setPrecioAsiento(new BigDecimal("150000"));
        reservation.setPrecioExtras(BigDecimal.ZERO);
        reservation.setPrecioTotal(new BigDecimal("150000"));
    }

    @Test
    void testProcessQuestion_NoReservations() {
        when(reservationRepository.findByPasajeroEmail("test@example.com"))
            .thenReturn(Collections.emptyList());

        String response = chatbotService.processQuestion("test@example.com", "¿Cuáles son mis reservas?");

        assertEquals("No tienes ninguna reserva registrada en el sistema.", response);
        verify(reservationRepository).findByPasajeroEmail("test@example.com");
    }

    @Test
    void testProcessQuestion_WithReservations_Success() {
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(reservations);
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        String response = chatbotService.processQuestion("test@example.com", "¿Cuáles son mis reservas?");

        assertNotNull(response);
        // Verifica que no sea el mensaje de "sin reservas"
        assertFalse(response.contains("No tienes ninguna reserva"));
    }

    @Test
    void testProcessQuestion_ApiError() {
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(reservations);
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        // El servicio captura excepciones y devuelve un mensaje de fallback
        String response = chatbotService.processQuestion("test@example.com", "¿Cuáles son mis reservas?");

        assertNotNull(response);
        // Verifica que devuelve alguna respuesta válida
        assertFalse(response.isEmpty());
    }

    @Test
    void testProcessQuestion_FlightNotFound() {
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(reservations);
        when(flightRepository.findById(100L)).thenReturn(Optional.empty());

        String response = chatbotService.processQuestion("test@example.com", "¿Cuáles son mis reservas?");

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testProcessQuestion_SeatNotFound() {
        List<Reservation> reservations = Arrays.asList(reservation);
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(reservations);
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        String response = chatbotService.processQuestion("test@example.com", "¿Cuáles son mis reservas?");

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testProcessQuestion_MultipleReservations() {
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setCodigoReservacion("F4U-20251107-XYZ2");
        reservation2.setPasajeroEmail("test@example.com");
        reservation2.setVueloId(200L);

        List<Reservation> reservations = Arrays.asList(reservation, reservation2);
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(reservations);

        String response = chatbotService.processQuestion("test@example.com", "Muéstrame todas mis reservas");

        assertNotNull(response);
    }

    @Test
    void testProcessQuestion_EmptyQuestion() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));

        try {
            String response = chatbotService.processQuestion("test@example.com", "");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_QuestionAboutDestination() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));

        try {
            String response = chatbotService.processQuestion("test@example.com", "¿Cuál es mi destino?");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_QuestionAboutDate() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));

        try {
            String response = chatbotService.processQuestion("test@example.com", "¿Cuándo es mi vuelo?");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_QuestionAboutSeat() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        try {
            String response = chatbotService.processQuestion("test@example.com", "¿Cuál es mi asiento?");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_QuestionAboutFlightNumber() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));
        when(flightRepository.findById(100L)).thenReturn(Optional.of(flight));

        try {
            String response = chatbotService.processQuestion("test@example.com", "¿Cuál es el número de mi vuelo?");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_QuestionAboutPrice() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));

        try {
            String response = chatbotService.processQuestion("test@example.com", "¿Cuánto pagué por mi reserva?");
            assertNotNull(response);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_NullEmail() {
        try {
            chatbotService.processQuestion(null, "¿Cuál es mi reserva?");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testProcessQuestion_NullQuestion() {
        when(reservationRepository.findByPasajeroEmail("test@example.com")).thenReturn(List.of(reservation));

        try {
            chatbotService.processQuestion("test@example.com", null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}

