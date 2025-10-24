package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que combina información del asiento con su estado de bloqueo
 */
public class SeatWithLockInfo {
    private Long id;
    private Long vueloId;
    private String numeroAsiento;
    private Integer fila;
    private String columna;
    private String clase;
    private String ubicacion;
    private Boolean disponible;
    private BigDecimal precio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Información de bloqueo
    private boolean locked;
    private long remainingLockSeconds;
    
    public SeatWithLockInfo(Seat seat, boolean locked, long remainingLockSeconds) {
        this.id = seat.getId();
        this.vueloId = seat.getVueloId();
        this.numeroAsiento = seat.getNumeroAsiento();
        this.fila = seat.getFila();
        this.columna = seat.getColumna();
        this.clase = seat.getClase();
        this.ubicacion = seat.getUbicacion();
        this.disponible = seat.getDisponible();
        this.precio = seat.getPrecio();
        this.fechaCreacion = seat.getFechaCreacion();
        this.fechaActualizacion = seat.getFechaActualizacion();
        this.locked = locked;
        this.remainingLockSeconds = remainingLockSeconds;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getVueloId() { return vueloId; }
    public void setVueloId(Long vueloId) { this.vueloId = vueloId; }
    
    public String getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(String numeroAsiento) { this.numeroAsiento = numeroAsiento; }
    
    public Integer getFila() { return fila; }
    public void setFila(Integer fila) { this.fila = fila; }
    
    public String getColumna() { return columna; }
    public void setColumna(String columna) { this.columna = columna; }
    
    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    
    public long getRemainingLockSeconds() { return remainingLockSeconds; }
    public void setRemainingLockSeconds(long remainingLockSeconds) { this.remainingLockSeconds = remainingLockSeconds; }
}
