package com.fly.company.f4u_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fly.company.f4u_backend.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Método existente
    List<Reservation> findByVueloId(Long vueloId);
    
    // MÉTODOS EXISTENTES FUNCIONALES
    List<Reservation> findByPasajeroEmail(String pasajeroEmail);
    
    List<Reservation> findByPasajeroEmailAndEstado(String pasajeroEmail, String estado);
    
    Optional<Reservation> findByCodigoReservacion(String codigoReservacion);
    
    // Consulta personalizada para obtener reservas con información de vuelo
    @Query("SELECT r FROM Reservation r WHERE r.pasajeroEmail = :email AND r.estado = 'CONFIRMADA' ORDER BY r.fechaReservacion DESC")
    List<Reservation> findConfirmedReservationsByEmail(@Param("email") String email);
    
    // Contar reservas por estado
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.pasajeroEmail = :email AND r.estado = :estado")
    Long countByPasajeroEmailAndEstado(@Param("email") String email, @Param("estado") String estado);

    // NUEVAS CONSULTAS CON INFORMACIÓN DE VUELOS (usando vueloId en lugar de relación JPA)
    @Query("SELECT r FROM Reservation r WHERE r.pasajeroEmail = :email ORDER BY r.fechaReservacion DESC")
    List<Reservation> findReservationsByEmail(@Param("email") String email);
    
    @Query("SELECT r FROM Reservation r WHERE r.pasajeroEmail = :email AND r.estado = :estado ORDER BY r.fechaReservacion DESC")
    List<Reservation> findReservationsByEmailAndEstado(@Param("email") String email, @Param("estado") String estado);
}