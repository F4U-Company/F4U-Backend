package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservaciones")
public class Reservation {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_reservacion", unique = true, length = 20)
    private String codigoReservacion;

    // Referencias
    @Column(name = "vuelo_id", nullable = false)
    private Long vueloId;

    @Column(name = "pasajero_id")
    private Long pasajeroId;

    @Column(name = "asiento_id", nullable = false)
    private Long asientoId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "extra_id", referencedColumnName = "id")
    private Extra extra;

    // Información del pasajero (desnormalizado para historial)
    @Column(name = "pasajero_nombre", nullable = false)
    private String pasajeroNombre;

    @Column(name = "pasajero_apellido", nullable = false)
    private String pasajeroApellido;

    @Column(name = "pasajero_email", nullable = false)
    private String pasajeroEmail;

    @Column(name = "pasajero_telefono", length = 20)
    private String pasajeroTelefono;

    @Column(name = "pasajero_documento_tipo", length = 20)
    private String pasajeroDocumentoTipo;

    @Column(name = "pasajero_documento_numero", length = 50)
    private String pasajeroDocumentoNumero;

    @Column(name = "pasajero_fecha_nacimiento")
    private LocalDate pasajeroFechaNacimiento;

    // Información de la reserva
    @Column(name = "clase", nullable = false, length = 50)
    private String clase; // PRIMERA_CLASE, EJECUTIVA, ECONOMICA

    @Column(name = "precio_asiento", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAsiento;

    @Column(name = "precio_extras", precision = 10, scale = 2)
    private BigDecimal precioExtras = BigDecimal.ZERO;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    // Estado
    @Column(name = "estado", nullable = false, length = 50)
    private String estado = "PENDIENTE_PAGO"; // PENDIENTE_PAGO, CONFIRMADA, etc.

    // Extras seleccionados
    @Column(name = "extra_maleta_cabina")
    private Boolean extraMaletaCabina = false;

    @Column(name = "extra_maleta_bodega")
    private Boolean extraMaletaBodega = false;

    @Column(name = "extra_seguro_50")
    private Boolean extraSeguro50 = false;

    @Column(name = "extra_seguro_100")
    private Boolean extraSeguro100 = false;

    @Column(name = "extra_asistencia_especial")
    private Boolean extraAsistenciaEspecial = false;

    // Información de pago
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "estado_pago", length = 50)
    private String estadoPago;

    // Fechas
    @Column(name = "fecha_reservacion", nullable = false)
    private LocalDateTime fechaReservacion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_check_in")
    private LocalDateTime fechaCheckIn;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "fecha_vencimiento_pago")
    private LocalDateTime fechaVencimientoPago;

    // Información adicional
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "origen_reserva", length = 50)
    private String origenReserva = "WEB";

    // Auditoría
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Reservation() {}

    // Constructor completo para crear reservas confirmadas/post pago
    public Reservation(Long vueloId,
                       Long asientoId,
                       Long pasajeroId,
                       String pasajeroNombre,
                       String pasajeroApellido,
                       String pasajeroEmail,
                       String pasajeroTelefono,
                       String pasajeroDocumentoTipo,
                       String pasajeroDocumentoNumero,
                       LocalDate pasajeroFechaNacimiento,
                       String clase,
                       BigDecimal precioAsiento,
                       BigDecimal precioExtras,
                       BigDecimal precioTotal,
                       Boolean extraMaletaCabina,
                       Boolean extraMaletaBodega,
                       Boolean extraSeguro50,
                       Boolean extraSeguro100,
                       Boolean extraAsistenciaEspecial,
                       String metodoPago,
                       String referenciaPago,
                       String estadoPago,
                       String estado,
                       String observaciones,
                       String origenReserva) {
        this.vueloId = vueloId;
        this.asientoId = asientoId;
        this.pasajeroId = pasajeroId;
        this.pasajeroNombre = pasajeroNombre;
        this.pasajeroApellido = pasajeroApellido;
        this.pasajeroEmail = pasajeroEmail;
        this.pasajeroTelefono = pasajeroTelefono;
        this.pasajeroDocumentoTipo = pasajeroDocumentoTipo;
        this.pasajeroDocumentoNumero = pasajeroDocumentoNumero;
        this.pasajeroFechaNacimiento = pasajeroFechaNacimiento;
        this.clase = clase;
        this.precioAsiento = precioAsiento;
        this.precioExtras = precioExtras != null ? precioExtras : BigDecimal.ZERO;
        this.precioTotal = precioTotal;
        this.extraMaletaCabina = extraMaletaCabina;
        this.extraMaletaBodega = extraMaletaBodega;
        this.extraSeguro50 = extraSeguro50;
        this.extraSeguro100 = extraSeguro100;
        this.extraAsistenciaEspecial = extraAsistenciaEspecial;
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
        this.estadoPago = estadoPago;
        this.estado = estado;
        this.observaciones = observaciones;
        this.origenReserva = origenReserva != null ? origenReserva : "WEB";
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaReservacion == null) {
            fechaReservacion = LocalDateTime.now();
        }
        // Establecer fecha de vencimiento de pago (15 minutos después)
        if (fechaVencimientoPago == null) {
            fechaVencimientoPago = LocalDateTime.now().plusMinutes(15);
        }
        // Si la reserva ya está confirmada (estado CONFIRMADA y estadoPago APROBADO) setear fechaPago
        if ("CONFIRMADA".equalsIgnoreCase(estado) && "APROBADO".equalsIgnoreCase(estadoPago)) {
            fechaPago = LocalDateTime.now();
        }
        // codigoReservacion: dejar nulo para que el trigger en BD lo genere si existe, o generar fallback
        if (codigoReservacion == null || codigoReservacion.isBlank()) {
            codigoReservacion = generarCodigoFallback();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    private String generarCodigoFallback() {
        // Formato F4U-YYYYMMDD-XXXX simple (fallback si trigger no existe)
        String fecha = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String random = Integer.toHexString((int)(Math.random() * 0xFFFF)).toUpperCase();
        while (random.length() < 4) random = "0" + random;
        return "F4U-" + fecha + "-" + random;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoReservacion() { return codigoReservacion; }
    public void setCodigoReservacion(String codigoReservacion) { this.codigoReservacion = codigoReservacion; }

    public Long getVueloId() { return vueloId; }
    public void setVueloId(Long vueloId) { this.vueloId = vueloId; }

    public Long getPasajeroId() { return pasajeroId; }
    public void setPasajeroId(Long pasajeroId) { this.pasajeroId = pasajeroId; }

    public Long getAsientoId() { return asientoId; }
    public void setAsientoId(Long asientoId) { this.asientoId = asientoId; }

    public Extra getExtra() { return extra; }
    public void setExtra(Extra extra) { this.extra = extra; }

    public String getPasajeroNombre() { return pasajeroNombre; }
    public void setPasajeroNombre(String pasajeroNombre) { this.pasajeroNombre = pasajeroNombre; }

    public String getPasajeroApellido() { return pasajeroApellido; }
    public void setPasajeroApellido(String pasajeroApellido) { this.pasajeroApellido = pasajeroApellido; }

    public String getPasajeroEmail() { return pasajeroEmail; }
    public void setPasajeroEmail(String pasajeroEmail) { this.pasajeroEmail = pasajeroEmail; }

    public String getPasajeroTelefono() { return pasajeroTelefono; }
    public void setPasajeroTelefono(String pasajeroTelefono) { this.pasajeroTelefono = pasajeroTelefono; }

    public String getPasajeroDocumentoTipo() { return pasajeroDocumentoTipo; }
    public void setPasajeroDocumentoTipo(String pasajeroDocumentoTipo) { this.pasajeroDocumentoTipo = pasajeroDocumentoTipo; }

    public String getPasajeroDocumentoNumero() { return pasajeroDocumentoNumero; }
    public void setPasajeroDocumentoNumero(String pasajeroDocumentoNumero) { this.pasajeroDocumentoNumero = pasajeroDocumentoNumero; }

    public LocalDate getPasajeroFechaNacimiento() { return pasajeroFechaNacimiento; }
    public void setPasajeroFechaNacimiento(LocalDate pasajeroFechaNacimiento) { this.pasajeroFechaNacimiento = pasajeroFechaNacimiento; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public BigDecimal getPrecioAsiento() { return precioAsiento; }
    public void setPrecioAsiento(BigDecimal precioAsiento) { this.precioAsiento = precioAsiento; }

    public BigDecimal getPrecioExtras() { return precioExtras; }
    public void setPrecioExtras(BigDecimal precioExtras) { this.precioExtras = precioExtras; }

    public BigDecimal getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(BigDecimal precioTotal) { this.precioTotal = precioTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getExtraMaletaCabina() { return extraMaletaCabina; }
    public void setExtraMaletaCabina(Boolean extraMaletaCabina) { this.extraMaletaCabina = extraMaletaCabina; }

    public Boolean getExtraMaletaBodega() { return extraMaletaBodega; }
    public void setExtraMaletaBodega(Boolean extraMaletaBodega) { this.extraMaletaBodega = extraMaletaBodega; }

    public Boolean getExtraSeguro50() { return extraSeguro50; }
    public void setExtraSeguro50(Boolean extraSeguro50) { this.extraSeguro50 = extraSeguro50; }

    public Boolean getExtraSeguro100() { return extraSeguro100; }
    public void setExtraSeguro100(Boolean extraSeguro100) { this.extraSeguro100 = extraSeguro100; }

    public Boolean getExtraAsistenciaEspecial() { return extraAsistenciaEspecial; }
    public void setExtraAsistenciaEspecial(Boolean extraAsistenciaEspecial) { this.extraAsistenciaEspecial = extraAsistenciaEspecial; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public LocalDateTime getFechaReservacion() { return fechaReservacion; }
    public void setFechaReservacion(LocalDateTime fechaReservacion) { this.fechaReservacion = fechaReservacion; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public LocalDateTime getFechaCheckIn() { return fechaCheckIn; }
    public void setFechaCheckIn(LocalDateTime fechaCheckIn) { this.fechaCheckIn = fechaCheckIn; }

    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(LocalDateTime fechaCancelacion) { this.fechaCancelacion = fechaCancelacion; }

    public LocalDateTime getFechaVencimientoPago() { return fechaVencimientoPago; }
    public void setFechaVencimientoPago(LocalDateTime fechaVencimientoPago) { this.fechaVencimientoPago = fechaVencimientoPago; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getOrigenReserva() { return origenReserva; }
    public void setOrigenReserva(String origenReserva) { this.origenReserva = origenReserva; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
