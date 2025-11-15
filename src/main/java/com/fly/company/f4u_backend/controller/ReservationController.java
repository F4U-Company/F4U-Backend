package com.fly.company.f4u_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.service.ReservationService;
import com.fly.company.f4u_backend.service.SeatLockService;

@RestController
@RequestMapping("/api/reservaciones")
public class ReservationController {

    private final SeatLockService seatLockService;
    private final ReservationService reservationService;

    public ReservationController(SeatLockService seatLockService, ReservationService reservationService) {
        this.seatLockService = seatLockService;
        this.reservationService = reservationService;
    }

    @PostMapping("/try-lock/{seatId}")
    public ResponseEntity<?> tryLock(@PathVariable Long seatId, @RequestBody(required = false) String userId) {
        // Si no se proporciona userId, generar uno temporal
        String user = (userId != null && !userId.isEmpty()) ? userId : "reservation-user-" + System.currentTimeMillis();
        boolean ok = seatLockService.tryLock(seatId, user);
        if (ok) return ResponseEntity.ok().build();
        else return ResponseEntity.status(409).body("Seat locked");
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest req) {
        try {
            Reservation r = reservationService.createReservation(req);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    // Endpoint adicional para mantener compatibilidad con frontend previo (inglés)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ReservationRequest req) {
        return createReservation(req);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> all() {
        return ResponseEntity.ok(reservationService.getAll());
    }
}
