package com.example.issairline.repository;

import com.example.issairline.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("select f from Flight f join fetch f.aircraft")
    List<Flight> findAllWithAircraft();

    @Query("select f from Flight f join fetch f.aircraft where f.id = :id")
    Optional<Flight> findByIdWithAircraft(@Param("id") Long id);
}
