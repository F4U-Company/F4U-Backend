package com.fly.company.f4u_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

    // ============ ENDPOINTS EXISTENTES ============

    /**
     * Obtener reservas del usuario actual basado en el token JWT
     * GET /api/reservaciones/usuario
     */
    @GetMapping("/usuario")
    public ResponseEntity<List<Reservation>> getUserReservations(@AuthenticationPrincipal Jwt jwt) {
        try {
            // Extraer email del token JWT
            String userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getClaimAsString("preferred_username");
            }
            
            if (userEmail == null) {
                return ResponseEntity.badRequest().build();
            }

            List<Reservation> reservations = reservationService.getReservationsByUserEmail(userEmail);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener reservas activas del usuario (CONFIRMADA)
     * GET /api/reservaciones/usuario/activas
     */
    @GetMapping("/usuario/activas")
    public ResponseEntity<List<Reservation>> getUserActiveReservations(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getClaimAsString("preferred_username");
            }
            
            if (userEmail == null) {
                return ResponseEntity.badRequest().build();
            }

            List<Reservation> activeReservations = reservationService.getActiveReservationsByUserEmail(userEmail);
            return ResponseEntity.ok(activeReservations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener estadísticas del usuario para el dashboard
     * GET /api/reservaciones/usuario/estadisticas
     */
    @GetMapping("/usuario/estadisticas")
    public ResponseEntity<Map<String, Object>> getUserStats(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getClaimAsString("preferred_username");
            }
            
            if (userEmail == null) {
                return ResponseEntity.badRequest().build();
            }

            Map<String, Object> stats = reservationService.getUserStats(userEmail);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener reserva por código
     * GET /api/reservaciones/{codigo}
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<Reservation> getReservationByCode(@PathVariable String codigo) {
        try {
            Reservation reservation = reservationService.getReservationByCode(codigo);
            if (reservation != null) {
                return ResponseEntity.ok(reservation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============ NUEVOS ENDPOINTS CON INFORMACIÓN BÁSICA ============

    /**
     * Obtener reservas del usuario con información básica ordenada
     * GET /api/reservaciones/usuario/completo
     */
    @GetMapping("/usuario/completo")
    public ResponseEntity<List<Reservation>> getUserReservationsWithBasicInfo(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getClaimAsString("preferred_username");
            }
            
            if (userEmail == null) {
                return ResponseEntity.badRequest().build();
            }

            List<Reservation> reservations = reservationService.getReservationsWithBasicInfoByEmail(userEmail);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtener reservas activas del usuario con información básica ordenada
     * GET /api/reservaciones/usuario/activas/completo
     */
    @GetMapping("/usuario/activas/completo")
    public ResponseEntity<List<Reservation>> getUserActiveReservationsWithBasicInfo(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userEmail = jwt.getClaimAsString("email");
            if (userEmail == null) {
                userEmail = jwt.getClaimAsString("preferred_username");
            }
            
            if (userEmail == null) {
                return ResponseEntity.badRequest().build();
            }

            List<Reservation> activeReservations = reservationService.getActiveReservationsWithBasicInfoByEmail(userEmail);
            return ResponseEntity.ok(activeReservations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}