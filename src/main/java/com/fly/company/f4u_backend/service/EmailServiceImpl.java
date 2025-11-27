package com.fly.company.f4u_backend.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fly.company.f4u_backend.model.Reservation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendReservationConfirmation(
            Reservation reservation,
            String flightNumber,
            String origin,
            String destination,
            String departureDate,
            String arrivalDate,
            String seatNumber) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reservation.getPasajeroEmail());
            helper.setSubject("Confirmación de Reserva - Vuelo " + flightNumber);

            String htmlContent = buildEmailTemplate(
                    reservation, flightNumber, origin, destination,
                    departureDate, arrivalDate, seatNumber);

            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("✓ Correo de confirmación enviado a: " + reservation.getPasajeroEmail());

        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
            e.printStackTrace();
            // No lanzamos excepción para que no falle la reserva si el correo falla
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildEmailTemplate(
            Reservation reservation,
            String flightNumber,
            String origin,
            String destination,
            String departureDate,
            String arrivalDate,
            String seatNumber) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        String clase = getClaseDescripcion(reservation.getClase());
        String extras = buildExtrasSection(reservation);

        // URL de la imagen de encabezado A1.png
        String headerImageUrl = "https://blogger.googleusercontent.com/img/a/AVvXsEhvw93_kr3Pz5yNSy_daW6pkBPu_XPeUr5bxJEkJbDLYr9fp0PRz0xcKkzkFnGmVQrfYt8X_yxsRNRf6wZqjtabB-1SDLplASih8ID3I8xw8TXH1JyrEVlShUhunlO7qtCtJAamEB_O9-QbiKklwOpMzVxdp7EPN1DVBVNXCa45fkVqmAjbLnqP-T94B90=s16000";

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmación de Reserva</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                    <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f4f4f4; padding: 30px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border: 1px solid #dddddd;">

                                    <!-- Header -->
                                    <tr>
                                        <td style="background-color: #ffffff; padding: 0; text-align: center; border-bottom: 3px solid #0066cc;">
                                            <img src="%s" alt="F4U Airlines" style="width: 100%%; max-width: 600px; height: auto; display: block;">
                                        </td>
                                    </tr>

                                    <!-- Greeting -->
                                    <tr>
                                        <td style="padding: 30px 30px 20px;">
                                            <p style="margin: 0; color: #333333; font-size: 16px; line-height: 1.5;">
                                                Estimado/a %s %s,
                                            </p>
                                            <p style="margin: 15px 0 0; color: #666666; font-size: 15px; line-height: 1.6;">
                                                Gracias por elegir Fly For You. Su reserva ha sido confirmada con éxito.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Reservation Code -->
                                    <tr>
                                        <td style="padding: 0 30px 25px;">
                                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f8f9fa; border: 1px solid #dee2e6; padding: 15px;">
                                                <tr>
                                                    <td>
                                                        <p style="margin: 0; color: #666666; font-size: 13px;">
                                                            Código de reserva
                                                        </p>
                                                        <p style="margin: 5px 0 0; color: #0066cc; font-size: 20px; font-weight: bold;">
                                                            %s
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Flight Details -->
                                    <tr>
                                        <td style="padding: 20px 30px;">
                                            <h2 style="margin: 0 0 15px; color: #333333; font-size: 18px; font-weight: bold;">
                                                Detalles del vuelo
                                            </h2>

                                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Vuelo</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Origen</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Destino</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Salida</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Llegada</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Asiento</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0;">
                                                        <span style="color: #666666; font-size: 14px;">Clase</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Passenger Info -->
                                    <tr>
                                        <td style="padding: 20px 30px;">
                                            <h2 style="margin: 0 0 15px; color: #333333; font-size: 18px; font-weight: bold;">
                                                Datos del pasajero
                                            </h2>

                                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Nombre</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s %s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Documento</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s %s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <span style="color: #666666; font-size: 14px;">Email</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0; border-bottom: 1px solid #eeeeee;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0;">
                                                        <span style="color: #666666; font-size: 14px;">Teléfono</span>
                                                    </td>
                                                    <td align="right" style="padding: 10px 0;">
                                                        <strong style="color: #333333; font-size: 14px;">%s</strong>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Extras Section (if any) -->
                                    %s

                                    <!-- Payment Summary -->
                                    <tr>
                                        <td style="padding: 20px 30px;">
                                            <h2 style="margin: 0 0 15px; color: #333333; font-size: 18px; font-weight: bold;">
                                                Resumen de pago
                                            </h2>
                                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f8f9fa; border: 1px solid #dee2e6; padding: 15px;">
                                                <tr>
                                                    <td>
                                                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                                                            <tr>
                                                                <td style="padding: 8px 0;">
                                                                    <span style="color: #666666; font-size: 14px;">Asiento</span>
                                                                </td>
                                                                <td align="right" style="padding: 8px 0;">
                                                                    <span style="color: #333333; font-size: 14px;">%s</span>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td style="padding: 8px 0; border-bottom: 1px solid #dee2e6;">
                                                                    <span style="color: #666666; font-size: 14px;">Extras</span>
                                                                </td>
                                                                <td align="right" style="padding: 8px 0; border-bottom: 1px solid #dee2e6;">
                                                                    <span style="color: #333333; font-size: 14px;">%s</span>
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td style="padding: 12px 0 0;">
                                                                    <span style="color: #333333; font-size: 16px; font-weight: bold;">Total</span>
                                                                </td>
                                                                <td align="right" style="padding: 12px 0 0;">
                                                                    <span style="color: #0066cc; font-size: 18px; font-weight: bold;">%s</span>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Important Info -->
                                    <tr>
                                        <td style="padding: 20px 30px;">
                                            <div style="background-color: #fff3cd; border: 1px solid #ffc107; padding: 15px;">
                                                <p style="margin: 0 0 10px; color: #333333; font-size: 14px; font-weight: bold;">
                                                    Información importante
                                                </p>
                                                <p style="margin: 0; color: #666666; font-size: 13px; line-height: 1.6;">
                                                    • Llegue al aeropuerto con 2 horas de anticipación<br>
                                                    • Presente su documento de identidad y código de reserva<br>
                                                    • Revise las políticas de equipaje en nuestro sitio web<br>
                                                    • Check-in online disponible 24 horas antes del vuelo
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #1e293b; padding: 30px 25px;">
                                            <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                                                <tr>
                                                    <td style="vertical-align: middle; text-align: left; width: 30%%;">
                                                        <h2 style="margin: 0; color: #ffffff; font-size: 24px; font-weight: bold;">
                                                            F4U
                                                        </h2>
                                                    </td>
                                                    <td style="vertical-align: middle; text-align: right; width: 70%%;">
                                                        <p style="margin: 0 0 8px; color: #ffffff; font-size: 15px;">
                                                            Gracias por volar con Fly For You
                                                        </p>
                                                        <p style="margin: 0; color: #ffffff; font-size: 13px; opacity: 0.9;">
                                                            ¿Preguntas? Contáctanos: f4uaerolinea@gmail.com
                                                        </p>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                        """
                .formatted(
                        headerImageUrl,
                        reservation.getPasajeroNombre(),
                        reservation.getPasajeroApellido(),
                        reservation.getCodigoReservacion(),
                        flightNumber,
                        origin,
                        destination,
                        departureDate,
                        arrivalDate,
                        seatNumber,
                        clase,
                        reservation.getPasajeroNombre(),
                        reservation.getPasajeroApellido(),
                        reservation.getPasajeroDocumentoTipo(),
                        reservation.getPasajeroDocumentoNumero(),
                        reservation.getPasajeroEmail(),
                        reservation.getPasajeroTelefono() != null ? reservation.getPasajeroTelefono() : "No registrado",
                        extras,
                        currencyFormat.format(reservation.getPrecioAsiento()),
                        currencyFormat.format(reservation.getPrecioExtras()),
                        currencyFormat.format(reservation.getPrecioTotal()));
    }

    private String getClaseDescripcion(String clase) {
        return switch (clase) {
            case "PRIMERA_CLASE" -> "Primera Clase";
            case "EJECUTIVA" -> "Clase Ejecutiva";
            case "ECONOMICA" -> "Clase Económica";
            default -> clase;
        };
    }

    private String buildExtrasSection(Reservation reservation) {
        StringBuilder extras = new StringBuilder();
        boolean hasExtras = false;

        if (Boolean.TRUE.equals(reservation.getExtraMaletaCabina()) ||
                Boolean.TRUE.equals(reservation.getExtraMaletaBodega()) ||
                Boolean.TRUE.equals(reservation.getExtraSeguro50()) ||
                Boolean.TRUE.equals(reservation.getExtraSeguro100()) ||
                Boolean.TRUE.equals(reservation.getExtraAsistenciaEspecial())) {

            hasExtras = true;
            extras.append("""
                        <tr>
                            <td style="padding: 20px 30px;">
                                <h2 style="margin: 0 0 15px; color: #333333; font-size: 18px; font-weight: bold;">
                                    Servicios adicionales
                                </h2>
                                <div style="background-color: #e7f3ff; border: 1px solid #b3d9ff; padding: 15px;">
                                    <p style="margin: 0; color: #333333; font-size: 14px; line-height: 1.8;">
                    """);

            if (Boolean.TRUE.equals(reservation.getExtraMaletaCabina())) {
                extras.append("• Maleta de cabina<br>");
            }
            if (Boolean.TRUE.equals(reservation.getExtraMaletaBodega())) {
                extras.append("• Maleta en bodega<br>");
            }
            if (Boolean.TRUE.equals(reservation.getExtraSeguro50())) {
                extras.append("• Seguro de viaje básico<br>");
            }
            if (Boolean.TRUE.equals(reservation.getExtraSeguro100())) {
                extras.append("• Seguro de viaje completo<br>");
            }
            if (Boolean.TRUE.equals(reservation.getExtraAsistenciaEspecial())) {
                extras.append("• Asistencia especial<br>");
            }

            extras.append("""
                                    </p>
                                </div>
                            </td>
                        </tr>
                    """);
        }

        return hasExtras ? extras.toString() : "";
    }
}
