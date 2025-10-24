package com.fly.company.f4u_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fly.company.f4u_backend.service.SeatLockService;

@RestController
@RequestMapping("/api/seat-locks")
public class SeatLockController {

    @Autowired
    private SeatLockService seatLockService;

    /**
     * Bloquear un asiento para un usuario por 15 minutos
     * POST /api/seat-locks/lock
     * Body: { "seatId": 123, "userId": "user-session-id" }
     */
    @PostMapping("/lock")
    public ResponseEntity<Map<String, Object>> lockSeat(@RequestBody Map<String, Object> request) {
        Long seatId = Long.valueOf(request.get("seatId").toString());
        String userId = request.get("userId").toString();
        
        boolean locked = seatLockService.tryLock(seatId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", locked);
        response.put("seatId", seatId);
        response.put("userId", userId);
        
        if (locked) {
            response.put("message", "Asiento bloqueado por 15 minutos");
            response.put("expiresInSeconds", 15 * 60);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "El asiento ya está bloqueado por otro usuario");
            return ResponseEntity.status(409).body(response); // 409 Conflict
        }
    }

    /**
     * Liberar manualmente el bloqueo de un asiento
     * DELETE /api/seat-locks/{seatId}
     */
    @DeleteMapping("/{seatId}")
    public ResponseEntity<Map<String, Object>> releaseLock(@PathVariable Long seatId) {
        seatLockService.releaseLock(seatId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Bloqueo liberado");
        response.put("seatId", seatId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si un asiento está bloqueado
     * GET /api/seat-locks/{seatId}/status
     */
    @GetMapping("/{seatId}/status")
    public ResponseEntity<Map<String, Object>> checkLockStatus(@PathVariable Long seatId) {
        boolean isLocked = seatLockService.isLocked(seatId);
        long remainingSeconds = seatLockService.getRemainingLockTimeSeconds(seatId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seatId", seatId);
        response.put("locked", isLocked);
        response.put("remainingSeconds", remainingSeconds);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener información general de todos los bloqueos
     * GET /api/seat-locks/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getLockInfo() {
        Map<String, Object> info = seatLockService.getLockInfo();
        return ResponseEntity.ok(info);
    }
}
