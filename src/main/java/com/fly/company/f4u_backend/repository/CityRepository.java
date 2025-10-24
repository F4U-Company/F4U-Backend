package com.fly.company.f4u_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fly.company.f4u_backend.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    
    /**
     * Buscar todas las ciudades activas
     */
    List<City> findByActivoTrue();
    
    /**
     * Buscar ciudad por código IATA
     */
    Optional<City> findByCodigoIata(String codigoIata);
    
    /**
     * Buscar ciudades por país
     */
    List<City> findByPaisAndActivoTrue(String pais);
    
    /**
     * Buscar ciudades por código de país
     */
    List<City> findByCodigoPaisAndActivoTrue(String codigoPais);
}
