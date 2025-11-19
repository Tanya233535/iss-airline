package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FlightService {

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
        if (id == null) {
            log.warn("Передан null вместо ID рейса");
            return Optional.empty();
        }
        return flightRepository.findById(id);
    }

    @Transactional
    public void save(Flight flight) {
        save(flight, false);
    }

    @Transactional
    public void save(Flight flight, boolean systemUpdate) {

        if (flight == null) {
            throw new IllegalArgumentException("Рейс не может быть null");
        }

        boolean isNew = (flight.getId() == null);
        Flight existingFlight = null;

        if (!isNew) {
            existingFlight = flightRepository.findById(flight.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));

            if (!systemUpdate &&
                    (existingFlight.getStatus() == Flight.Status.ARRIVED ||
                            existingFlight.getStatus() == Flight.Status.DEPARTED)) {

                throw new IllegalStateException("Нельзя изменять завершённые рейсы!");
            }
        }

        validateFlightTimes(flight);

        handleAircraftHours(flight, existingFlight);

        try {
            flightRepository.save(flight);
            log.info("Сохранён рейс {} (ID={})", flight.getFlightNo(), flight.getId());
        } catch (DataIntegrityViolationException e) {
            log.warn("Нарушение ограничений БД при сохранении рейса {}", flight.getFlightNo());
            throw new IllegalStateException("Невозможно сохранить рейс — проверьте корректность данных.");
        } catch (Exception e) {
            log.error("Ошибка сохранения рейса {}", flight.getFlightNo(), e);
            throw new IllegalStateException("Произошла ошибка при сохранении рейса.");
        }
    }

    @Transactional
    public void deleteById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID рейса не может быть пустым");
        }

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));

        if (flight.getStatus() == Flight.Status.DEPARTED) {
            throw new IllegalStateException("Нельзя удалить рейс, который находится в полёте!");
        }

        adjustAircraftHoursOnDelete(flight);

        try {
            flightRepository.delete(flight);
            log.info("Рейс {} (ID={}) успешно удалён", flight.getFlightNo(), flight.getId());
        } catch (Exception e) {
            log.error("Ошибка при удалении рейса {}", id, e);
            throw new IllegalStateException("Не удалось удалить рейс!");
        }
    }

    private void validateFlightTimes(Flight flight) {
        if (flight.getScheduledDeparture() != null && flight.getScheduledArrival() != null) {

            if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) {
                throw new IllegalArgumentException("Дата прибытия не может быть раньше вылета!");
            }

            Duration duration = Duration.between(
                    flight.getScheduledDeparture(),
                    flight.getScheduledArrival()
            );

            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();

            flight.setRouteDuration(String.format("%d ч %d мин", hours, minutes));
        }
    }

    private void handleAircraftHours(Flight flight, Flight existingFlight) {

        if (flight.getAircraft() == null) return;

        Aircraft aircraft = flight.getAircraft();

        if (aircraft.getStatus() == Aircraft.Status.MAINTENANCE) {
            throw new IllegalArgumentException("Самолёт находится на техобслуживании!");
        }

        double currentHours = aircraft.getTotalFlightHours() == null ? 0.0 : aircraft.getTotalFlightHours();
        double flightHours = calculateFlightHours(flight);

        if (flight.getStatus() == Flight.Status.ARRIVED) {

            boolean wasNotArrived =
                    existingFlight == null || existingFlight.getStatus() != Flight.Status.ARRIVED;

            if (wasNotArrived) {
                aircraft.setTotalFlightHours(currentHours + flightHours);
                aircraftRepository.save(aircraft);

                log.info("Налёт самолёта {} увеличен на {} ч (итого {})",
                        aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
            }
        }

        if (existingFlight != null &&
                existingFlight.getStatus() == Flight.Status.ARRIVED &&
                flight.getStatus() != Flight.Status.ARRIVED) {

            aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));
            aircraftRepository.save(aircraft);

            log.info("Налёт самолёта {} уменьшен на {} ч (итого {})",
                    aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
        }
    }

    private void adjustAircraftHoursOnDelete(Flight flight) {
        if (flight.getAircraft() == null) return;

        if (flight.getStatus() == Flight.Status.ARRIVED) {
            Aircraft aircraft = flight.getAircraft();

            double currentHours = aircraft.getTotalFlightHours() == null ? 0 : aircraft.getTotalFlightHours();
            double flightHours = calculateFlightHours(flight);

            aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));
            aircraftRepository.save(aircraft);

            log.info("Удаление рейса: корректировка налёта самолёта {}: -{} ч (итого {} ч)",
                    aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
        }
    }

    private double calculateFlightHours(Flight flight) {
        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null) return 0;

        Duration dur = Duration.between(
                flight.getScheduledDeparture(),
                flight.getScheduledArrival()
        );

        return Math.round((dur.toMinutes() / 60.0) * 10.0) / 10.0;
    }
}
