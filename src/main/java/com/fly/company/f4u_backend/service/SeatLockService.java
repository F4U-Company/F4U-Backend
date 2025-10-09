package com.fly.company.f4u_backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeatLockService {
    private final Map<Long, Long> locks = new ConcurrentHashMap<>();
    private final long LOCK_MS = 5 * 60 * 1000; // 5 minutos

    /**
     * Intenta bloquear un seatId. Devuelve true si se logra bloquear.
     */
    public boolean tryLock(Long seatId) {
        long now = Instant.now().toEpochMilli();
        locks.compute(seatId, (k, expiry) -> {
            if (expiry == null || expiry < now) {
                return now + LOCK_MS;
            } else {
                return expiry;
            }
        });
        return locks.get(seatId) > now;
    }

    public void releaseLock(Long seatId) {
        locks.remove(seatId);
    }

    public boolean isLocked(Long seatId) {
        Long expiry = locks.get(seatId);
        return expiry != null && expiry > Instant.now().toEpochMilli();
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        long now = Instant.now().toEpochMilli();
        locks.entrySet().removeIf(e -> e.getValue() < now);
    }
}
