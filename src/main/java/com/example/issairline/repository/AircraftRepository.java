package com.example.issairline.repository;

import com.example.issairline.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для доступа к данным о самолётах.
 * Использует Spring Data JPA для работы с таблицей aircrafts.
 */
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, String> {
}

