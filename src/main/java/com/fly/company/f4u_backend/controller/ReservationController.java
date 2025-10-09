package com.fly.company.f4u_backend.controller;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.service.ReservationService;
import com.fly.company.f4u_backend.service.SeatLockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final SeatLockService seatLockService;
    private final ReservationService reservationService;

    public ReservationController(SeatLockService seatLockService, ReservationService reservationService) {
        this.seatLockService = seatLockService;
        this.reservationService = reservationService;
    }

    @PostMapping("/try-lock/{seatId}")
    public ResponseEntity<?> tryLock(@PathVariable Long seatId) {
        boolean ok = seatLockService.tryLock(seatId);
        if (ok) return ResponseEntity.ok().build();
        else return ResponseEntity.status(409).body("Seat locked");
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ReservationRequest req) {
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

    @GetMapping
    public ResponseEntity<List<Reservation>> all() {
        return ResponseEntity.ok(reservationService.getAll());
    }
}
