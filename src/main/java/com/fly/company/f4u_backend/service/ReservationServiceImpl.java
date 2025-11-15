package com.fly.company.f4u_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            SeatRepository seatRepository,
            SeatLockService seatLockService) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.seatLockService = seatLockService;
    }

    @Override
    @Transactional
    public Reservation createReservation(ReservationRequest req) {
        // Validaciones básicas
        Optional<Seat> maybeSeat = seatRepository.findById(req.getAsientoId());
        if (maybeSeat.isEmpty()) {
            throw new IllegalArgumentException("Seat not found");
        }
        Seat seat = maybeSeat.get();
        if (!seat.getDisponible()) {
            throw new IllegalStateException("Seat already assigned");
        }
        if (seatLockService.isLocked(req.getAsientoId()) == false) {
            // Si no estaba bloqueado, no permitir confirmar (regla simple)
            throw new IllegalStateException("Seat is not locked (lock before creating reservation)");
        }

        // Marcar seat como NO disponible (ocupado)
        seat.setDisponible(false);
        seatRepository.save(seat);

        // Construir entidad Reservation con todos los campos (post pago)
        // Reglas de negocio para extras
        String clase = req.getClase();
        boolean primera = "PRIMERA_CLASE".equalsIgnoreCase(clase);
        boolean ejecutiva = "EJECUTIVA".equalsIgnoreCase(clase);

        // Calcular precioExtras ignorando lo que venga del request por seguridad
        java.math.BigDecimal extras = java.math.BigDecimal.ZERO;
        if (!primera) { // Primera clase incluye todo sin costo
            if (Boolean.TRUE.equals(req.getExtraMaletaCabina())) {
                extras = extras.add(java.math.BigDecimal.valueOf(25000));
            }
            if (Boolean.TRUE.equals(req.getExtraMaletaBodega()) && !ejecutiva) { // ejecutiva incluye maleta bodega
                extras = extras.add(java.math.BigDecimal.valueOf(45000));
            }
            if (Boolean.TRUE.equals(req.getExtraSeguro50())) {
                extras = extras.add(java.math.BigDecimal.valueOf(35000));
            }
            if (Boolean.TRUE.equals(req.getExtraSeguro100())) {
                extras = extras.add(java.math.BigDecimal.valueOf(60000));
            }
            if (Boolean.TRUE.equals(req.getExtraAsistenciaEspecial())) {
                extras = extras.add(java.math.BigDecimal.valueOf(50000));
            }
        }

        java.math.BigDecimal precioAsiento = req.getPrecioAsiento();
        if (precioAsiento == null) {
            throw new IllegalArgumentException("precioAsiento es requerido");
        }
        java.math.BigDecimal precioTotal = precioAsiento.add(extras);

        // Flags definitivos (incluidos automáticamente en primera / maleta bodega en ejecutiva)
        boolean maletaCabina = primera || Boolean.TRUE.equals(req.getExtraMaletaCabina());
        boolean maletaBodega = primera || ejecutiva || Boolean.TRUE.equals(req.getExtraMaletaBodega());
        boolean seguro50 = primera || Boolean.TRUE.equals(req.getExtraSeguro50());
        boolean seguro100 = primera || Boolean.TRUE.equals(req.getExtraSeguro100());
        boolean asistencia = primera || Boolean.TRUE.equals(req.getExtraAsistenciaEspecial());

        Reservation res = new Reservation(
            req.getVueloId(),
            req.getAsientoId(),
            req.getPasajeroId(),
            req.getPasajeroNombre(),
            req.getPasajeroApellido(),
            req.getPasajeroEmail(),
            req.getPasajeroTelefono(),
            req.getPasajeroDocumentoTipo(),
            req.getPasajeroDocumentoNumero(),
            req.getPasajeroFechaNacimiento(),
            req.getClase(),
            precioAsiento,
            extras,
            precioTotal,
            maletaCabina,
            maletaBodega,
            seguro50,
            seguro100,
            asistencia,
            req.getMetodoPago(),
            req.getReferenciaPago(),
            req.getEstadoPago(),
            req.getEstado() != null ? req.getEstado() : "CONFIRMADA",
            req.getObservaciones(),
            req.getOrigenReserva()
        );

        Reservation saved = reservationRepository.save(res);

        // liberar lock
        seatLockService.releaseLock(req.getAsientoId());

        return saved;
    }

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getByFlight(Long vueloId) {
        return reservationRepository.findByVueloId(vueloId);
    }
}
