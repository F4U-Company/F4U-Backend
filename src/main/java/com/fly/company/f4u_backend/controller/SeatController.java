package com.fly.company.f4u_backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.model.SeatWithLockInfo;
import com.fly.company.f4u_backend.repository.SeatRepository;
import com.fly.company.f4u_backend.service.SeatLockService;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private SeatLockService seatLockService;

    /**
     * Obtener todos los asientos de un vuelo específico con información de bloqueos
     */
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<SeatWithLockInfo>> getSeatsByFlight(@PathVariable Long flightId) {
        List<Seat> seats = seatRepository.findByVueloId(flightId);
        
        // Agregar información de bloqueos a cada asiento
        List<SeatWithLockInfo> seatsWithLocks = seats.stream()
            .map(seat -> {
                boolean locked = seatLockService.isLocked(seat.getId());
                long remainingSeconds = seatLockService.getRemainingLockTimeSeconds(seat.getId());
                return new SeatWithLockInfo(seat, locked, remainingSeconds);
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(seatsWithLocks);
    }

    /**
     * Reservar un asiento (marcarlo como NO disponible)
     */
    @PutMapping("/{seatId}/reserve")
    public ResponseEntity<Seat> reserveSeat(@PathVariable Long seatId) {
        return seatRepository.findById(seatId)
            .map(seat -> {
                if (!seat.getDisponible()) {
                    return ResponseEntity.badRequest().<Seat>build();
                }
                seat.setDisponible(false);
                Seat updatedSeat = seatRepository.save(seat);
                return ResponseEntity.ok(updatedSeat);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Liberar un asiento (marcarlo como disponible)
     */
    @PutMapping("/{seatId}/release")
    public ResponseEntity<Seat> releaseSeat(@PathVariable Long seatId) {
        return seatRepository.findById(seatId)
            .map(seat -> {
                seat.setDisponible(true);
                Seat updatedSeat = seatRepository.save(seat);
                return ResponseEntity.ok(updatedSeat);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
