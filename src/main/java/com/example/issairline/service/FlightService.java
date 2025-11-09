package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;
import com.example.issairline.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        boolean isNew = (flight.getId() == null);
        Flight existingFlight = null;

        if (!isNew) {
            existingFlight = flightRepository.findById(flight.getId()).orElse(null);
        }

        if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {
            if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) {
                throw new IllegalArgumentException("Дата прибытия не может быть раньше вылета!");
            }

            Duration duration = Duration.between(flight.getScheduledDeparture(), flight.getScheduledArrival());
            flight.setRouteDuration(String.valueOf(duration));

            if (flight.getAircraft() != null) {
                var aircraft = flight.getAircraft();

                if (aircraft.getStatus() == Aircraft.Status.MAINTENANCE) {
                    throw new IllegalArgumentException("Самолёт находится на техобслуживании!");
                }

                int currentHours = (aircraft.getTotalFlightHours() == null ? 0 : aircraft.getTotalFlightHours());
                int newHours = (int) duration.toHours();

                //Добавляем налёт только если рейс впервые стал ARRIVED
                if (flight.getStatus() == Flight.Status.ARRIVED) {
                    if (isNew || (existingFlight != null && existingFlight.getStatus() != Flight.Status.ARRIVED)) {
                        aircraft.setTotalFlightHours(currentHours + newHours);
                    }
                }

                // Если рейс был ARRIVED, но стал другим статусом — вычитаем налёт
                if (existingFlight != null && existingFlight.getStatus() == Flight.Status.ARRIVED
                        && flight.getStatus() != Flight.Status.ARRIVED) {
                    aircraft.setTotalFlightHours(Math.max(0, currentHours - newHours));
                }
            }
        }

        flightRepository.save(flight);
    }

    public void deleteById(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);

        if (flight != null && flight.getStatus() == Flight.Status.ARRIVED && flight.getAircraft() != null) {
            var aircraft = flight.getAircraft();
            int hours = 0;

            try {
                Duration duration = Duration.parse(flight.getRouteDuration());
                hours = (int) duration.toHours();
            } catch (Exception ignored) {
            }

            int currentHours = (aircraft.getTotalFlightHours() == null ? 0 : aircraft.getTotalFlightHours());
            aircraft.setTotalFlightHours(Math.max(0, currentHours - hours));
        }

        flightRepository.deleteById(id);
    }
}
