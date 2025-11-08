package com.example.issairline.repository;

import com.example.issairline.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с таблицей flights
 */
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
}

