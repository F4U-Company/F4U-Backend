package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.Reservation;

/**
 * Servicio para envío de correos electrónicos
 */
public interface EmailService {

    /**
     * Envía un correo de confirmación de reserva al pasajero
     * 
     * @param reservation   La reserva confirmada
     * @param flightNumber  El número de vuelo
     * @param origin        Ciudad de origen
     * @param destination   Ciudad de destino
     * @param departureDate Fecha de salida
     * @param arrivalDate   Fecha de llegada
     * @param seatNumber    Número de asiento
     */
    void sendReservationConfirmation(
            Reservation reservation,
            String flightNumber,
            String origin,
            String destination,
            String departureDate,
            String arrivalDate,
            String seatNumber);
}
