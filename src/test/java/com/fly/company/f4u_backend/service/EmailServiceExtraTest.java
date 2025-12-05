package com.fly.company.f4u_backend.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.fly.company.f4u_backend.model.Reservation;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceExtraTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendReservationConfirmationCallsCreateMimeMessage() {
        Reservation reservation = new Reservation();
        reservation.setId(100L);
        reservation.setPasajeroEmail("test@example.com");
        reservation.setPasajeroNombre("Juan");
        reservation.setPasajeroApellido("Pérez");
        reservation.setCodigoReservacion("RES123");
        reservation.setPrecioTotal(new BigDecimal("500.00"));

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> {
            emailService.sendReservationConfirmation(
                reservation, "AA123", "Bogotá", "Miami",
                "2024-01-15", "2024-01-15", "12A"
            );
        });

        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendReservationConfirmationWithRuntimeException() {
        Reservation reservation = new Reservation();
        reservation.setPasajeroEmail("fail@test.com");

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Unexpected error"));

        assertDoesNotThrow(() -> {
            emailService.sendReservationConfirmation(
                reservation, "CC789", "Lima", "Buenos Aires",
                "2024-03-10", "2024-03-10", "8D"
            );
        });
    }

    @Test
    void testSendReservationConfirmationWithNullEmail() {
        Reservation reservation = new Reservation();
        reservation.setPasajeroEmail(null);
        reservation.setPrecioTotal(null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> {
            emailService.sendReservationConfirmation(
                reservation, null, null, null, null, null, null
            );
        });
    }

    @Test
    void testSendMultipleReservationConfirmations() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        for (int i = 0; i < 3; i++) {
            Reservation r = new Reservation();
            r.setPasajeroEmail("user" + i + "@test.com");
            r.setCodigoReservacion("RES" + i);

            emailService.sendReservationConfirmation(
                r, "FL" + i, "Origin", "Dest", "2024-01-01", "2024-01-01", "1A"
            );
        }

        verify(mailSender, times(3)).createMimeMessage();
    }

    @Test
    void testSendReservationConfirmationWithDifferentData() {
        Reservation r1 = new Reservation();
        r1.setPasajeroEmail("user1@test.com");
        r1.setCodigoReservacion("CODE1");
        r1.setPrecioTotal(new BigDecimal("1000.00"));

        Reservation r2 = new Reservation();
        r2.setPasajeroEmail("user2@test.com");
        r2.setCodigoReservacion("CODE2");
        r2.setPrecioTotal(new BigDecimal("2000.00"));

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendReservationConfirmation(r1, "F1", "A", "B", "2024-01-01", "2024-01-01", "1A");
        emailService.sendReservationConfirmation(r2, "F2", "C", "D", "2024-02-02", "2024-02-02", "2B");

        verify(mailSender, times(2)).createMimeMessage();
    }
}
