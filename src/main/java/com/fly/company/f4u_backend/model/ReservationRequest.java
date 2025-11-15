package com.fly.company.f4u_backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO extendido para crear una reservación completa post-pago
public class ReservationRequest {
    // Identificadores
    private Long vueloId;
    private Long asientoId;
    private Long pasajeroId; // opcional

    // Pasajero (desnormalizado)
    private String pasajeroNombre;
    private String pasajeroApellido;
    private String pasajeroEmail;
    private String pasajeroTelefono;
    private String pasajeroDocumentoTipo;
    private String pasajeroDocumentoNumero;
    private LocalDate pasajeroFechaNacimiento;

    // Reserva
    private String clase;
    private BigDecimal precioAsiento;
    private BigDecimal precioExtras;
    private BigDecimal precioTotal;

    // Extras
    private Boolean extraMaletaCabina;
    private Boolean extraMaletaBodega;
    private Boolean extraSeguro50;
    private Boolean extraSeguro100;
    private Boolean extraAsistenciaEspecial;

    // Pago
    private String metodoPago;
    private String referenciaPago;
    private String estadoPago; // PENDIENTE/APROBADO

    // Estado
    private String estado; // PENDIENTE_PAGO / CONFIRMADA

    // Misceláneo
    private String observaciones;
    private String origenReserva;

    public ReservationRequest() {}

    // Getters/Setters
    public Long getVueloId() { return vueloId; }
    public void setVueloId(Long vueloId) { this.vueloId = vueloId; }
    public Long getAsientoId() { return asientoId; }
    public void setAsientoId(Long asientoId) { this.asientoId = asientoId; }
    public Long getPasajeroId() { return pasajeroId; }
    public void setPasajeroId(Long pasajeroId) { this.pasajeroId = pasajeroId; }
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
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getOrigenReserva() { return origenReserva; }
    public void setOrigenReserva(String origenReserva) { this.origenReserva = origenReserva; }
}
