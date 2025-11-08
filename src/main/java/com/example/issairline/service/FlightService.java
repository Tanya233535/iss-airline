package com.example.issairline.service;

import com.example.issairline.entity.Flight;
import com.example.issairline.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для логики работы с рейсами
 */
@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id);
    }

    public void save(Flight flight) {
        flightRepository.save(flight);
    }

    public void deleteById(Long id) {
        flightRepository.deleteById(id);
    }
}

