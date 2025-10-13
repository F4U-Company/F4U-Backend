package com.fly.company.f4u_backend.service;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing reservations with enhanced seat locking.
 * Ensures thread-safe reservation creation with proper lock validation.
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

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
        logger.info("Creating reservation for seat {} with session {}", req.getSeatId(), req.getSessionId());
        
        // Validate request parameters
        if (req.getSeatId() == null) {
            throw new IllegalArgumentException("Seat ID is required");
        }
        if (req.getSessionId() == null || req.getSessionId().trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID is required");
        }
        if (req.getPassengerName() == null || req.getPassengerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name is required");
        }
        if (req.getPassengerEmail() == null || req.getPassengerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger email is required");
        }

        // Check if seat exists
        Optional<Seat> maybeSeat = seatRepository.findById(req.getSeatId());
        if (maybeSeat.isEmpty()) {
            logger.warn("Seat {} not found", req.getSeatId());
            throw new IllegalArgumentException("Seat not found");
        }
        
        Seat seat = maybeSeat.get();
        
        // Check if seat is already assigned
        if (seat.isAssigned()) {
            logger.warn("Seat {} is already assigned", req.getSeatId());
            throw new IllegalStateException("Seat already assigned");
        }
        
        // Validate that the seat is locked by the requesting session
        if (!seatLockService.isLocked(req.getSeatId())) {
            logger.warn("Seat {} is not locked", req.getSeatId());
            throw new IllegalStateException("Seat is not locked. Please lock the seat before confirming reservation.");
        }
        
        // Verify that the lock is owned by the requesting session
        Optional<com.fly.company.f4u_backend.model.SeatLock> lockInfo = seatLockService.getLockInfo(req.getSeatId());
        if (lockInfo.isEmpty() || !req.getSessionId().equals(lockInfo.get().getSessionId())) {
            logger.warn("Seat {} lock is not owned by session {}", req.getSeatId(), req.getSessionId());
            throw new IllegalStateException("Seat lock is not owned by the requesting session");
        }

        // Check if there's already a reservation for this seat
        List<Reservation> existingReservations = reservationRepository.findBySeatId(req.getSeatId());
        if (!existingReservations.isEmpty()) {
            logger.warn("Reservation already exists for seat {}", req.getSeatId());
            throw new IllegalStateException("Reservation already exists for this seat");
        }

        try {
            // Mark seat as assigned
            seat.setAssigned(true);
            seatRepository.save(seat);
            logger.info("Marked seat {} as assigned", req.getSeatId());

            // Create reservation
            Reservation reservation = new Reservation(req.getFlightId(), req.getSeatId(), 
                    req.getPassengerName(), req.getPassengerEmail(), "CONFIRMED");
            Reservation saved = reservationRepository.save(reservation);
            logger.info("Created reservation {} for seat {}", saved.getId(), req.getSeatId());

            // Release the lock after successful reservation
            boolean lockReleased = seatLockService.releaseLock(req.getSeatId(), req.getSessionId());
            if (lockReleased) {
                logger.info("Released lock for seat {} after successful reservation", req.getSeatId());
            } else {
                logger.warn("Failed to release lock for seat {} after reservation", req.getSeatId());
            }

            return saved;
            
        } catch (Exception e) {
            logger.error("Error creating reservation for seat {}: {}", req.getSeatId(), e.getMessage(), e);
            
            // Attempt to rollback seat assignment if reservation creation failed
            try {
                seat.setAssigned(false);
                seatRepository.save(seat);
                logger.info("Rolled back seat {} assignment due to reservation creation failure", req.getSeatId());
            } catch (Exception rollbackException) {
                logger.error("Failed to rollback seat {} assignment: {}", req.getSeatId(), rollbackException.getMessage());
            }
            
            throw e;
        }
    }

    @Override
    public List<Reservation> getAll() {
        try {
            return reservationRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving all reservations: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Reservation> getByFlight(Long flightId) {
        if (flightId == null) {
            throw new IllegalArgumentException("Flight ID is required");
        }
        
        try {
            return reservationRepository.findByFlightId(flightId);
        } catch (Exception e) {
            logger.error("Error retrieving reservations for flight {}: {}", flightId, e.getMessage(), e);
            throw e;
        }
    }
}
