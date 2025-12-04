package com.fly.company.f4u_backend.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.ReservationRequest;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.FlightRepository;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final SeatLockService seatLockService;
    private final EmailService emailService;
    private final FlightRepository flightRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            SeatRepository seatRepository,
            SeatLockService seatLockService,
            EmailService emailService,
            FlightRepository flightRepository) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.seatLockService = seatLockService;
        this.emailService = emailService;
        this.flightRepository = flightRepository;
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
        
        // Verificar que el asiento esté bloqueado
        if (!seatLockService.isLocked(req.getAsientoId())) {
            throw new IllegalStateException("Seat is not locked (lock before creating reservation)");
        }
        
        // NUEVA VALIDACIÓN: Verificar que el usuario que crea la reserva sea el mismo que bloqueó el asiento
        // Para usuarios autenticados: usamos su email como userId
        // Para usuarios NO autenticados: el frontend envía el lockUserId temporal que usó para bloquear
        String userIdToCheck = req.getLockUserId(); // ID temporal usado en el bloqueo
        if (userIdToCheck == null || userIdToCheck.isEmpty()) {
            // Fallback: usar email si no se envió lockUserId (usuarios autenticados)
            userIdToCheck = req.getPasajeroEmail();
        }
        if (userIdToCheck == null || userIdToCheck.isEmpty()) {
            throw new IllegalArgumentException("Email del pasajero es requerido");
        }
        
        // Logs de depuración
        String lockedBy = seatLockService.getLockedByUserId(req.getAsientoId());
        System.out.println("🔍 DEBUG VALIDACIÓN LOCK:");
        System.out.println("   - AsientoId: " + req.getAsientoId());
        System.out.println("   - lockUserId recibido: " + req.getLockUserId());
        System.out.println("   - pasajeroEmail recibido: " + req.getPasajeroEmail());
        System.out.println("   - userIdToCheck final: " + userIdToCheck);
        System.out.println("   - Bloqueado por (backend): " + lockedBy);
        System.out.println("   - ¿Coinciden?: " + (lockedBy != null && lockedBy.equals(userIdToCheck)));
        
        if (!seatLockService.isLockedByUser(req.getAsientoId(), userIdToCheck)) {
            System.err.println("❌ VALIDACIÓN FALLIDA: Lock no coincide");
            throw new IllegalStateException("Este asiento está siendo reservado por otro usuario. Por favor selecciona otro.");
        }
        System.out.println("✅ VALIDACIÓN EXITOSA: Usuario correcto");

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

        // Flags definitivos (incluidos automáticamente en primera / maleta bodega en
        // ejecutiva)
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
                req.getOrigenReserva());

        Reservation saved = reservationRepository.save(res);

        // liberar lock
        seatLockService.releaseLock(req.getAsientoId());

        // Enviar correo de confirmación
        try {
            sendConfirmationEmail(saved, seat);
        } catch (Exception e) {
            System.err.println("Error al enviar correo de confirmación: " + e.getMessage());
            // No lanzamos excepción para que no falle la reserva si el correo falla
        }

        return saved;
    }

    /**
     * Envía el correo de confirmación de reserva
     */
    private void sendConfirmationEmail(Reservation reservation, Seat seat) {
        // Obtener información del vuelo
        Optional<Flight> flightOpt = flightRepository.findById(reservation.getVueloId());
        if (flightOpt.isEmpty()) {
            System.err.println("No se pudo enviar correo: vuelo no encontrado");
            return;
        }

        Flight flight = flightOpt.get();

        // Formatear fechas en español
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy 'a las' HH:mm",
                Locale.forLanguageTag("es-ES"));
        String departureDate = flight.getFechaSalida().format(formatter);
        String arrivalDate = flight.getFechaLlegada().format(formatter);

        // Obtener nombres de ciudades
        String origin = flight.getCiudadOrigen() != null ? flight.getCiudadOrigen().getNombre() : "N/A";
        String destination = flight.getCiudadDestino() != null ? flight.getCiudadDestino().getNombre() : "N/A";

        // Obtener número de asiento
        String seatNumber = seat.getNumeroAsiento() != null ? seat.getNumeroAsiento() : "N/A";

        // Enviar correo
        emailService.sendReservationConfirmation(
                reservation,
                flight.getNumeroVuelo(),
                origin,
                destination,
                departureDate,
                arrivalDate,
                seatNumber);
    }

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> getByFlight(Long vueloId) {
        return reservationRepository.findByVueloId(vueloId);
    }

    // ============ MÉTODOS EXISTENTES ============

    @Override
    public List<Reservation> getReservationsByUserEmail(String email) {
        return reservationRepository.findByPasajeroEmail(email);
    }

    @Override
    public List<Reservation> getActiveReservationsByUserEmail(String email) {
        return reservationRepository.findByPasajeroEmailAndEstado(email, "CONFIRMADA");
    }

    @Override
    public Map<String, Object> getUserStats(String email) {
        Map<String, Object> stats = new HashMap<>();
        
        System.out.println("📊 [getUserStats] Buscando estadísticas para email: " + email);

        // Total de reservas (todas, sin importar estado)
        List<Reservation> allReservations = reservationRepository.findByPasajeroEmail(email);
        System.out.println("📊 [getUserStats] Total de reservas encontradas: " + allReservations.size());
        
        // Si no encuentra por email exacto, intentar con todas las reservas para debug
        if (allReservations.isEmpty()) {
            System.out.println("⚠️ [getUserStats] No se encontraron reservas con email exacto, buscando todas las reservas...");
            List<Reservation> todasReservas = reservationRepository.findAll();
            System.out.println("📋 Total de reservas en BD: " + todasReservas.size());
            if (!todasReservas.isEmpty()) {
                System.out.println("📋 Emails encontrados en BD:");
                todasReservas.forEach(r -> System.out.println("  - " + r.getPasajeroEmail() + " (Estado: " + r.getEstado() + ")"));
            }
        }
        
        stats.put("totalReservations", allReservations.size());

        // Reservas activas (CONFIRMADA o PAGADA)
        List<Reservation> activeReservations = reservationRepository.findByPasajeroEmail(email).stream()
                .filter(r -> "CONFIRMADA".equals(r.getEstado()) || "PAGADA".equals(r.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        stats.put("activeReservations", activeReservations.size());
        System.out.println("📊 [getUserStats] Reservas activas: " + activeReservations.size());

        // Millas acumuladas (1000 millas por cada reserva, activa o no)
        long accumulatedMiles = allReservations.size() * 1000L;
        stats.put("accumulatedMiles", accumulatedMiles);

        // Nivel basado en total de reservas
        String level = "Bronce";
        if (allReservations.size() >= 10) {
            level = "Oro";
        } else if (allReservations.size() >= 5) {
            level = "Plata";
        }
        stats.put("level", level);
        
        System.out.println("📊 [getUserStats] Stats finales: " + stats);

        // Próximo vuelo (si hay reservas activas)
        if (!activeReservations.isEmpty()) {
            // Ordenar por fecha de reservación para obtener el más reciente
            Reservation nextReservation = activeReservations.stream()
                    .sorted((r1, r2) -> r2.getFechaReservacion().compareTo(r1.getFechaReservacion()))
                    .findFirst()
                    .orElse(null);
            if (nextReservation != null) {
                stats.put("nextFlightDate", nextReservation.getFechaReservacion());
            }
        }

        return stats;
    }

    @Override
    public Reservation getReservationByCode(String codigo) {
        Optional<Reservation> reservation = reservationRepository.findByCodigoReservacion(codigo);
        return reservation.orElse(null);
    }

    // ============ NUEVOS MÉTODOS CON INFORMACIÓN BÁSICA ============

    @Override
    public List<Reservation> getReservationsWithBasicInfoByEmail(String email) {
        return reservationRepository.findReservationsByEmail(email);
    }

    @Override
    public List<Reservation> getActiveReservationsWithBasicInfoByEmail(String email) {
        return reservationRepository.findReservationsByEmailAndEstado(email, "CONFIRMADA");
    }
}