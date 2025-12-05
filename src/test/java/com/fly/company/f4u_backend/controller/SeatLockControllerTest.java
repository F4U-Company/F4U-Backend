package com.fly.company.f4u_backend.controller;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.fly.company.f4u_backend.service.SeatLockService;

@ExtendWith(MockitoExtension.class)
public class SeatLockControllerTest {

    @Mock
    private SeatLockService seatLockService;

    @InjectMocks
    private SeatLockController seatLockController;

    @Test
    public void testLockSeat_Success() {
        when(seatLockService.tryLock(anyLong(), anyString())).thenReturn(true);

        Map<String, Object> request = new HashMap<>();
        request.put("seatId", 1L);
        request.put("userId", "user123");

        ResponseEntity<Map<String, Object>> response = seatLockController.lockSeat(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue((Boolean) response.getBody().get("success"));
    }

    @Test
    public void testLockSeat_Conflict() {
        when(seatLockService.tryLock(anyLong(), anyString())).thenReturn(false);

        Map<String, Object> request = new HashMap<>();
        request.put("seatId", 1L);
        request.put("userId", "user123");

        ResponseEntity<Map<String, Object>> response = seatLockController.lockSeat(request);

        assertNotNull(response);
        assertEquals(409, response.getStatusCodeValue());
        assertFalse((Boolean) response.getBody().get("success"));
    }

    @Test
    public void testReleaseLock() {
        doNothing().when(seatLockService).releaseLock(anyLong());

        ResponseEntity<Map<String, Object>> response = seatLockController.releaseLock(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(seatLockService, times(1)).releaseLock(1L);
    }
}
