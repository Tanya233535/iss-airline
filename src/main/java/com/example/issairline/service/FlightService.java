package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;

    public FlightService(FlightRepository flightRepository, AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
    }

    public List<Flight> findAll() {
        return flightRepository.findAll();
    }

    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id);
    }

    @Transactional
    public void save(Flight flight) {
        save(flight, false);
    }

    @Transactional
    public void save(Flight flight, boolean systemUpdate) {
        boolean isNew = (flight.getId() == null);
        Flight existingFlight = null;

        if (!isNew) {
            existingFlight = flightRepository.findById(flight.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Рейс не найден!"));

            if (!systemUpdate &&
                    (existingFlight.getStatus() == Flight.Status.ARRIVED ||
                            existingFlight.getStatus() == Flight.Status.DEPARTED)) {
                throw new IllegalStateException("Нельзя изменять завершённые рейсы!");
            }
        }

        // Проверка корректности времени
        if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {
            if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) {
                throw new IllegalArgumentException("Дата прибытия не может быть раньше вылета!");
            }

            Duration duration = Duration.between(flight.getScheduledDeparture(), flight.getScheduledArrival());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            flight.setRouteDuration(String.format("%d ч %d мин", hours, minutes));
        }

        // Проверка самолёта
        if (flight.getAircraft() != null) {
            Aircraft aircraft = flight.getAircraft();

            if (aircraft.getStatus() == Aircraft.Status.MAINTENANCE) {
                throw new IllegalArgumentException("Самолёт находится на техобслуживании!");
            }

            double currentHours = (aircraft.getTotalFlightHours() == null ? 0.0 : aircraft.getTotalFlightHours());
            double flightHours = 0.0;
            if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {
                Duration dur = Duration.between(
                        flight.getScheduledDeparture(), flight.getScheduledArrival()
                );
                flightHours = Math.round((dur.toMinutes() / 60.0) * 10.0) / 10.0;
            }

            // Прибавляем налёт, если рейс впервые стал ARRIVED
            if (flight.getStatus() == Flight.Status.ARRIVED) {
                boolean wasNotArrived = existingFlight == null || existingFlight.getStatus() != Flight.Status.ARRIVED;
                if (wasNotArrived) {
                    aircraft.setTotalFlightHours(currentHours + flightHours);
                    aircraftRepository.save(aircraft);
                    log.info("Обновлён налёт самолёта {}: +{} ч (итого {} ч)",
                            aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
                }
            }

            // Если рейс был ARRIVED, но стал другим статусом — вычитаем налёт
            if (existingFlight != null && existingFlight.getStatus() == Flight.Status.ARRIVED
                    && flight.getStatus() != Flight.Status.ARRIVED) {
                aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));
                aircraftRepository.save(aircraft);
                log.info("Корректировка налёта самолёта {}: -{} ч (итого {} ч)",
                        aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
            }
        }

        flightRepository.save(flight);
    }

    @Transactional
    public void deleteById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейс не найден!"));

        if (flight.getStatus() == Flight.Status.DEPARTED) {
            throw new IllegalStateException("Нельзя удалить рейс, который находится в полёте!");
        }

        // Если рейс прибыл — вычитаем налёт
        if (flight.getStatus() == Flight.Status.ARRIVED && flight.getAircraft() != null) {
            Aircraft aircraft = flight.getAircraft();
            double currentHours = (aircraft.getTotalFlightHours() == null ? 0.0 : aircraft.getTotalFlightHours());
            double flightHours = 0.0;
            if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {
                Duration dur = Duration.between(flight.getScheduledDeparture(), flight.getScheduledArrival());
                flightHours = Math.round((dur.toMinutes() / 60.0) * 10.0) / 10.0;
            }

            aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));
            aircraftRepository.save(aircraft);

            log.info("Удалён рейс {} — скорректирован налёт самолёта {}: -{} ч (итого {} ч)",
                    flight.getFlightNo(),
                    aircraft.getAircraftCode(),
                    flightHours,
                    aircraft.getTotalFlightHours());
        }

        flightRepository.delete(flight);
        log.info("Рейс {} (id={}) успешно удалён.", flight.getFlightNo(), flight.getId());
    }
}
