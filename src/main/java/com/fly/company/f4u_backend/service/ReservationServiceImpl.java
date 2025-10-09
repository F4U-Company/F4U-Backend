package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            SeatRepository seatRepository,
            SeatLockService seatLockService) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.seatLockService = seatLockService;
    }

    @Override
    @Transactional
    public Reservation createReservation(ReservationRequest req) {
        // Validaciones básicas
        Optional<Seat> maybeSeat = seatRepository.findById(req.getSeatId());
        if (maybeSeat.isEmpty()) {
            throw new IllegalArgumentException("Seat not found");
        }
        Seat seat = maybeSeat.get();
        if (seat.isAssigned()) {
            throw new IllegalStateException("Seat already assigned");
        }
        if (seatLockService.isLocked(req.getSeatId()) == false) {
            // Si no estaba bloqueado, no permitir confirmar (regla simple)
            throw new IllegalStateException("Seat is not locked (lock before creating reservation)");
        }

        // Marcar seat como asignado
        seat.setAssigned(true);
        seatRepository.save(seat);

        Reservation res = new Reservation(req.getFlightId(), req.getSeatId(), req.getPassengerName(),
                req.getPassengerEmail(), "CONFIRMED");
        Reservation saved = reservationRepository.save(res);

        // liberar lock
        seatLockService.releaseLock(req.getSeatId());

        return saved;
    }

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getByFlight(Long flightId) {
        return reservationRepository.findByFlightId(flightId);
    }
}
