package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "vuelos")
public class Flight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_vuelo", nullable = false, length = 10)
    private String numeroVuelo;
    
    @Column(name = "aerolinea_id")
    private Long aerolineaId;
    
    @Column(name = "avion_id")
    private Long avionId;
    
    @ManyToOne
    @JoinColumn(name = "ciudad_origen_id", nullable = false)
    private City ciudadOrigen;
    
    @ManyToOne
    @JoinColumn(name = "ciudad_destino_id", nullable = false)
    private City ciudadDestino;
    
    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;
    
    @Column(name = "fecha_llegada", nullable = false)
    private LocalDateTime fechaLlegada;
    
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;
    
    @Column(name = "precio_economica", precision = 10, scale = 2)
    private BigDecimal precioEconomica;
    
    @Column(name = "precio_ejecutiva", precision = 10, scale = 2)
    private BigDecimal precioEjecutiva;
    
    @Column(name = "precio_primera_clase", precision = 10, scale = 2)
    private BigDecimal precioPrimeraClase;
    
    @Column(name = "asientos_disponibles_economica")
    private Integer asientosDisponiblesEconomica;
    
    @Column(name = "asientos_disponibles_ejecutiva")
    private Integer asientosDisponiblesEjecutiva;
    
    @Column(name = "asientos_disponibles_primera_clase")
    private Integer asientosDisponiblesPrimeraClase;
    
    @Column(length = 20)
    private String estado;
    
    @Column(name = "puerta_embarque", length = 5)
    private String puertaEmbarque;
    
    @Column(length = 5)
    private String terminal;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructors
    public Flight() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroVuelo() {
        return numeroVuelo;
    }
    
    public void setNumeroVuelo(String numeroVuelo) {
        this.numeroVuelo = numeroVuelo;
    }
    
    public Long getAerolineaId() {
        return aerolineaId;
    }
    
    public void setAerolineaId(Long aerolineaId) {
        this.aerolineaId = aerolineaId;
    }
    
    public Long getAvionId() {
        return avionId;
    }
    
    public void setAvionId(Long avionId) {
        this.avionId = avionId;
    }
    
    public City getCiudadOrigen() {
        return ciudadOrigen;
    }
    
    public void setCiudadOrigen(City ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }
    
    public City getCiudadDestino() {
        return ciudadDestino;
    }
    
    public void setCiudadDestino(City ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }
    
    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }
    
    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
    
    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }
    
    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }
    
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    
    public BigDecimal getPrecioEconomica() {
        return precioEconomica;
    }
    
    public void setPrecioEconomica(BigDecimal precioEconomica) {
        this.precioEconomica = precioEconomica;
    }
    
    public BigDecimal getPrecioEjecutiva() {
        return precioEjecutiva;
    }
    
    public void setPrecioEjecutiva(BigDecimal precioEjecutiva) {
        this.precioEjecutiva = precioEjecutiva;
    }
    
    public BigDecimal getPrecioPrimeraClase() {
        return precioPrimeraClase;
    }
    
    public void setPrecioPrimeraClase(BigDecimal precioPrimeraClase) {
        this.precioPrimeraClase = precioPrimeraClase;
    }
    
    public Integer getAsientosDisponiblesEconomica() {
        return asientosDisponiblesEconomica;
    }
    
    public void setAsientosDisponiblesEconomica(Integer asientosDisponiblesEconomica) {
        this.asientosDisponiblesEconomica = asientosDisponiblesEconomica;
    }
    
    public Integer getAsientosDisponiblesEjecutiva() {
        return asientosDisponiblesEjecutiva;
    }
    
    public void setAsientosDisponiblesEjecutiva(Integer asientosDisponiblesEjecutiva) {
        this.asientosDisponiblesEjecutiva = asientosDisponiblesEjecutiva;
    }
    
    public Integer getAsientosDisponiblesPrimeraClase() {
        return asientosDisponiblesPrimeraClase;
    }
    
    public void setAsientosDisponiblesPrimeraClase(Integer asientosDisponiblesPrimeraClase) {
        this.asientosDisponiblesPrimeraClase = asientosDisponiblesPrimeraClase;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getPuertaEmbarque() {
        return puertaEmbarque;
    }
    
    public void setPuertaEmbarque(String puertaEmbarque) {
        this.puertaEmbarque = puertaEmbarque;
    }
    
    public String getTerminal() {
        return terminal;
    }
    
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
