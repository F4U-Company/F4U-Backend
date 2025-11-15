package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "extras")
public class Extra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "maleta_cabina")
    private Boolean maletaCabina = false;

    @Column(name = "maleta_bodega")
    private Boolean maletaBodega = false;

    @Column(name = "seguro_50")
    private Boolean seguro50 = false; // Seguro 50% para cambio de itinerario

    @Column(name = "seguro_100")
    private Boolean seguro100 = false; // Seguro 100% para reembolsos

    @Column(name = "asistencia_especial")
    private Boolean asistenciaEspecial = false;

    @Column(name = "precio_total")
    private BigDecimal precioTotal;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Extra() {}

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public Boolean getMaletaCabina() { return maletaCabina; }
    public void setMaletaCabina(Boolean maletaCabina) { this.maletaCabina = maletaCabina; }

    public Boolean getMaletaBodega() { return maletaBodega; }
    public void setMaletaBodega(Boolean maletaBodega) { this.maletaBodega = maletaBodega; }

    public Boolean getSeguro50() { return seguro50; }
    public void setSeguro50(Boolean seguro50) { this.seguro50 = seguro50; }

    public Boolean getSeguro100() { return seguro100; }
    public void setSeguro100(Boolean seguro100) { this.seguro100 = seguro100; }

    public Boolean getAsistenciaEspecial() { return asistenciaEspecial; }
    public void setAsistenciaEspecial(Boolean asistenciaEspecial) { this.asistenciaEspecial = asistenciaEspecial; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
