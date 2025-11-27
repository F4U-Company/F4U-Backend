package com.fly.company.f4u_backend.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SeatLockService {
    private static final Logger log = LoggerFactory.getLogger(SeatLockService.class);

    // Map que guarda: seatId -> información del bloqueo
    private final Map<Long, SeatLock> locks = new ConcurrentHashMap<>();

    // Duración del bloqueo: 15 minutos
    private final long LOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutos

    // Clase interna para almacenar información del bloqueo
    private static class SeatLock {
        final long expiryTime;
        final String userId; // Opcional: para rastrear quién bloqueó
        final Instant lockedAt;

        SeatLock(long expiryTime, String userId) {
            this.expiryTime = expiryTime;
            this.userId = userId;
            this.lockedAt = Instant.now();
        }
    }

    private final SimpMessagingTemplate messagingTemplate;

    public SeatLockService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private void publishLockEvent(Long seatId, String type, String userId, long remainingSeconds) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("seatId", seatId);
            payload.put("type", type); // locked | unlocked | renewed | expired
            if (userId != null)
                payload.put("lockedByUserId", userId);
            payload.put("remainingSeconds", remainingSeconds);
            messagingTemplate.convertAndSend("/topic/seat-locks", payload);
        } catch (Exception e) {
            log.warn("No se pudo publicar evento de lock por websocket: {}", e.getMessage());
        }
    }

    /**
     * Intenta bloquear un asiento. Retorna true si se logra bloquear.
     * Si el asiento ya está bloqueado por otro usuario y no ha expirado, retorna
     * false.
     */
    public boolean tryLock(Long seatId, String userId) {
        long now = Instant.now().toEpochMilli();
        long expiryTime = now + LOCK_DURATION_MS;

        SeatLock existingLock = locks.get(seatId);

        // Si no hay bloqueo o el bloqueo expiró, crear nuevo bloqueo
        if (existingLock == null || existingLock.expiryTime < now) {
            locks.put(seatId, new SeatLock(expiryTime, userId));
            log.info("🔒 Asiento {} bloqueado por usuario {} por 15 minutos", seatId, userId);
            publishLockEvent(seatId, "locked", userId, (LOCK_DURATION_MS / 1000));
            return true;
        }

        // Si el mismo usuario intenta bloquear de nuevo, renovar el bloqueo
        if (existingLock.userId.equals(userId)) {
            locks.put(seatId, new SeatLock(expiryTime, userId));
            log.info("🔄 Bloqueo del asiento {} renovado para usuario {}", seatId, userId);
            publishLockEvent(seatId, "renewed", userId, (LOCK_DURATION_MS / 1000));
            return true;
        }

        // El asiento está bloqueado por otro usuario
        log.warn("❌ Asiento {} ya está bloqueado por otro usuario", seatId);
        return false;
    }

    /**
     * Libera manualmente el bloqueo de un asiento
     */
    public void releaseLock(Long seatId) {
        SeatLock removed = locks.remove(seatId);
        if (removed != null) {
            log.info("🔓 Bloqueo liberado para asiento {}", seatId);
            publishLockEvent(seatId, "unlocked", removed.userId, 0);
        }
    }

    /**
     * Verifica si un asiento está actualmente bloqueado
     */
    public boolean isLocked(Long seatId) {
        SeatLock lock = locks.get(seatId);
        if (lock == null) {
            return false;
        }

        long now = Instant.now().toEpochMilli();
        boolean locked = lock.expiryTime > now;

        // Si el bloqueo expiró, limpiarlo
        if (!locked) {
            locks.remove(seatId);
        }

        return locked;
    }

    /**
     * Obtiene el tiempo restante del bloqueo en segundos
     */
    public long getRemainingLockTimeSeconds(Long seatId) {
        SeatLock lock = locks.get(seatId);
        if (lock == null) {
            return 0;
        }

        long now = Instant.now().toEpochMilli();
        long remaining = lock.expiryTime - now;

        return remaining > 0 ? remaining / 1000 : 0;
    }

    /**
     * Obtiene el ID del usuario que bloqueó el asiento
     */
    public String getLockedByUserId(Long seatId) {
        SeatLock lock = locks.get(seatId);
        if (lock == null) {
            return null;
        }

        long now = Instant.now().toEpochMilli();
        // Solo retornar el userId si el bloqueo aún está activo
        return lock.expiryTime > now ? lock.userId : null;
    }

    /**
     * Verifica si un asiento está bloqueado por un usuario específico
     */
    public boolean isLockedByUser(Long seatId, String userId) {
        if (userId == null) {
            return false;
        }

        SeatLock lock = locks.get(seatId);
        if (lock == null) {
            return false;
        }

        long now = Instant.now().toEpochMilli();
        // Verificar que el bloqueo no haya expirado y que sea del usuario correcto
        boolean isActive = lock.expiryTime > now;
        boolean isSameUser = lock.userId != null && lock.userId.equals(userId);

        // Si el bloqueo expiró, limpiarlo
        if (!isActive) {
            locks.remove(seatId);
            return false;
        }

        return isSameUser;
    }

    /**
     * Obtiene información sobre todos los bloqueos activos
     */
    public Map<String, Object> getLockInfo() {
        Map<String, Object> info = new HashMap<>();
        long now = Instant.now().toEpochMilli();

        int activeLocksCount = 0;
        for (Map.Entry<Long, SeatLock> entry : locks.entrySet()) {
            if (entry.getValue().expiryTime > now) {
                activeLocksCount++;
            }
        }

        info.put("totalLocks", locks.size());
        info.put("activeLocks", activeLocksCount);
        info.put("lockDurationMinutes", LOCK_DURATION_MS / 60000);

        return info;
    }

    /**
     * Tarea programada que limpia bloqueos expirados cada 30 segundos
     */
    @Scheduled(fixedRate = 30_000) // Cada 30 segundos
    public void cleanupExpiredLocks() {
        long now = Instant.now().toEpochMilli();
        int removed = 0;

        var iterator = locks.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().expiryTime < now) {
                // publicar expiración antes de remover
                publishLockEvent(entry.getKey(), "expired", entry.getValue().userId, 0);
                iterator.remove();
                removed++;
            }
        }

        if (removed > 0) {
            log.info("🧹 Limpieza automática: {} bloqueos expirados eliminados", removed);
        }
    }
}
