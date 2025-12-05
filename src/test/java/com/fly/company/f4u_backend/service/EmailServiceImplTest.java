package com.fly.company.f4u_backend.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import com.fly.company.f4u_backend.model.Reservation;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Reservation reservation;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage((Session) null);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCodigoReservacion("F4U-20251107-ABC1");
        reservation.setPasajeroEmail("test@example.com");
        reservation.setPasajeroNombre("Juan");
        reservation.setPasajeroApellido("Pérez");
        reservation.setPasajeroTelefono("3001234567");
        reservation.setPasajeroDocumentoTipo("CC");
        reservation.setPasajeroDocumentoNumero("123456789");
        reservation.setClase("economica");
        reservation.setPrecioAsiento(new BigDecimal("150000"));
        reservation.setPrecioExtras(BigDecimal.ZERO);
        reservation.setPrecioTotal(new BigDecimal("150000"));
    }

    @Test
    void testSendReservationConfirmation_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendReservationConfirmation(
            reservation,
            "F4U-001",
            "Bogotá",
            "Medellín",
            "2025-12-15 10:00",
            "2025-12-15 12:00",
            "10A"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendReservationConfirmation_MessagingException() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // No debe lanzar excepción
        emailService.sendReservationConfirmation(
            reservation,
            "F4U-001",
            "Bogotá",
            "Medellín",
            "2025-12-15 10:00",
            "2025-12-15 12:00",
            "10A"
        );

        verify(mailSender).createMimeMessage();
    }

    @Test
    void testSendReservationConfirmation_WithExtras() {
        reservation.setExtraMaletaCabina(true);
        reservation.setExtraMaletaBodega(true);
        reservation.setExtraAsistenciaEspecial(true);
        reservation.setPrecioExtras(new BigDecimal("50000"));
        reservation.setPrecioTotal(new BigDecimal("200000"));

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendReservationConfirmation(
            reservation,
            "F4U-001",
            "Bogotá",
            "Medellín",
            "2025-12-15 10:00",
            "2025-12-15 12:00",
            "10A"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendReservationConfirmation_ClasePrimera() {
        reservation.setClase("primera");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendReservationConfirmation(
            reservation,
            "F4U-001",
            "Bogotá",
            "Medellín",
            "2025-12-15 10:00",
            "2025-12-15 12:00",
            "1A"
        );

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendReservationConfirmation_ClaseEjecutiva() {
        reservation.setClase("ejecutiva");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendReservationConfirmation(
            reservation,
            "F4U-001",
            "Bogotá",
            "Medellín",
            "2025-12-15 10:00",
            "2025-12-15 12:00",
            "5A"
        );

        verify(mailSender).send(any(MimeMessage.class));
    }
}
