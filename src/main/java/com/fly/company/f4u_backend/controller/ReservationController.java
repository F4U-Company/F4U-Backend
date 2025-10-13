package com.fly.company.f4u_backend.controller;

import com.fly.company.f4u_backend.model.*;
import com.fly.company.f4u_backend.service.ReservationService;
import com.fly.company.f4u_backend.service.SeatLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing seat reservations and locking operations.
 * Implements the seat locking workflow to prevent double assignment during booking.
 */
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final SeatLockService seatLockService;
    private final ReservationService reservationService;

    public ReservationController(SeatLockService seatLockService, ReservationService reservationService) {
        this.seatLockService = seatLockService;
        this.reservationService = reservationService;
    }

    /**
     * Attempts to lock a seat for the specified session.
     * This is the first step in the booking process.
     * 
     * @param request The seat lock request containing seatId, sessionId, and lockedBy
     * @param httpRequest HTTP request for extracting client information
     * @return Response indicating success or failure of the lock operation
     */
    @PostMapping("/lock")
    public ResponseEntity<SeatLockResponse> lockSeat(@RequestBody SeatLockRequest request, 
                                                    HttpServletRequest httpRequest) {
        try {
            logger.info("Attempting to lock seat {} for session {}", request.getSeatId(), request.getSessionId());
            
            // Extract client IP if not provided
            String lockedBy = request.getLockedBy();
            if (lockedBy == null || lockedBy.trim().isEmpty()) {
                lockedBy = getClientIpAddress(httpRequest);
            }
            
            boolean success = seatLockService.tryLock(request.getSeatId(), request.getSessionId(), lockedBy);
            
            if (success) {
                Optional<SeatLock> lockInfo = seatLockService.getLockInfo(request.getSeatId());
                SeatLockResponse response = new SeatLockResponse(true, "Seat locked successfully", lockInfo.orElse(null));
                logger.info("Successfully locked seat {} for session {}", request.getSeatId(), request.getSessionId());
                return ResponseEntity.ok(response);
            } else {
                SeatLockResponse response = new SeatLockResponse(false, "Seat is already locked by another session");
                logger.warn("Failed to lock seat {} - already locked", request.getSeatId());
                return ResponseEntity.status(409).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error locking seat {}: {}", request.getSeatId(), e.getMessage(), e);
            SeatLockResponse response = new SeatLockResponse(false, "Internal error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Releases a lock for a specific seat and session.
     * 
     * @param seatId The ID of the seat to unlock
     * @param sessionId The session ID that owns the lock
     * @return Response indicating success or failure of the unlock operation
     */
    @PostMapping("/unlock/{seatId}")
    public ResponseEntity<SeatLockResponse> unlockSeat(@PathVariable Long seatId, 
                                                      @RequestParam String sessionId) {
        try {
            logger.info("Attempting to unlock seat {} for session {}", seatId, sessionId);
            
            boolean success = seatLockService.releaseLock(seatId, sessionId);
            
            if (success) {
                SeatLockResponse response = new SeatLockResponse(true, "Seat unlocked successfully");
                logger.info("Successfully unlocked seat {} for session {}", seatId, sessionId);
                return ResponseEntity.ok(response);
            } else {
                SeatLockResponse response = new SeatLockResponse(false, "Lock not found or not owned by session");
                logger.warn("Failed to unlock seat {} for session {} - lock not found or not owned", seatId, sessionId);
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error unlocking seat {}: {}", seatId, e.getMessage(), e);
            SeatLockResponse response = new SeatLockResponse(false, "Internal error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Extends the lock duration for a seat if it's owned by the specified session.
     * 
     * @param seatId The ID of the seat
     * @param sessionId The session ID that owns the lock
     * @return Response indicating success or failure of the lock extension
     */
    @PostMapping("/extend-lock/{seatId}")
    public ResponseEntity<SeatLockResponse> extendLock(@PathVariable Long seatId, 
                                                      @RequestParam String sessionId) {
        try {
            logger.info("Attempting to extend lock for seat {} by session {}", seatId, sessionId);
            
            boolean success = seatLockService.extendLock(seatId, sessionId);
            
            if (success) {
                Optional<SeatLock> lockInfo = seatLockService.getLockInfo(seatId);
                SeatLockResponse response = new SeatLockResponse(true, "Lock extended successfully", lockInfo.orElse(null));
                logger.info("Successfully extended lock for seat {} by session {}", seatId, sessionId);
                return ResponseEntity.ok(response);
            } else {
                SeatLockResponse response = new SeatLockResponse(false, "Lock not found or not owned by session");
                logger.warn("Failed to extend lock for seat {} by session {} - lock not found or not owned", seatId, sessionId);
                return ResponseEntity.status(404).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error extending lock for seat {}: {}", seatId, e.getMessage(), e);
            SeatLockResponse response = new SeatLockResponse(false, "Internal error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Gets the current lock information for a seat.
     * 
     * @param seatId The ID of the seat
     * @return Response containing lock information if locked, empty if not locked
     */
    @GetMapping("/lock-info/{seatId}")
    public ResponseEntity<SeatLockResponse> getLockInfo(@PathVariable Long seatId) {
        try {
            Optional<SeatLock> lockInfo = seatLockService.getLockInfo(seatId);
            
            if (lockInfo.isPresent()) {
                SeatLockResponse response = new SeatLockResponse(true, "Seat is locked", lockInfo.get());
                return ResponseEntity.ok(response);
            } else {
                SeatLockResponse response = new SeatLockResponse(false, "Seat is not locked");
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            logger.error("Error getting lock info for seat {}: {}", seatId, e.getMessage(), e);
            SeatLockResponse response = new SeatLockResponse(false, "Internal error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Confirms a reservation and releases the seat lock.
     * This is the final step in the booking process.
     * 
     * @param req The reservation request
     * @return Response containing the created reservation or error message
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReservation(@RequestBody ReservationRequest req) {
        try {
            logger.info("Attempting to confirm reservation for seat {} by session {}", 
                       req.getSeatId(), req.getSessionId());
            
            Reservation reservation = reservationService.createReservation(req);
            
            logger.info("Successfully confirmed reservation {} for seat {}", 
                       reservation.getId(), req.getSeatId());
            
            return ResponseEntity.ok(reservation);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid reservation request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            logger.warn("Reservation conflict: {}", e.getMessage());
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error confirming reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    /**
     * Gets all reservations.
     * 
     * @return List of all reservations
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAll();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            logger.error("Error getting all reservations: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Gets reservations for a specific flight.
     * 
     * @param flightId The ID of the flight
     * @return List of reservations for the flight
     */
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Reservation>> getReservationsByFlight(@PathVariable Long flightId) {
        try {
            List<Reservation> reservations = reservationService.getByFlight(flightId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            logger.error("Error getting reservations for flight {}: {}", flightId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Generates a new session ID for seat locking.
     * 
     * @return A unique session identifier
     */
    @GetMapping("/session-id")
    public ResponseEntity<String> generateSessionId() {
        try {
            String sessionId = seatLockService.generateSessionId();
            return ResponseEntity.ok(sessionId);
        } catch (Exception e) {
            logger.error("Error generating session ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Gets statistics about active locks.
     * 
     * @return Number of currently active locks
     */
    @GetMapping("/lock-stats")
    public ResponseEntity<Integer> getLockStats() {
        try {
            int activeLocks = seatLockService.getActiveLockCount();
            return ResponseEntity.ok(activeLocks);
        } catch (Exception e) {
            logger.error("Error getting lock stats: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Extracts the client IP address from the HTTP request.
     * 
     * @param request The HTTP request
     * @return The client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
