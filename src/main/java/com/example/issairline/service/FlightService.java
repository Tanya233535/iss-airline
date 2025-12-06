package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.repository.FlightRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final AircraftService aircraftService;

    @Value("${aircraft.maintenance.threshold-hours}")
    private long thresholdHours;

    @Value("${aircraft.maintenance.period-days}")
    private long periodDays;

    public FlightService(FlightRepository flightRepository,
                         AircraftRepository aircraftRepository,
                         AircraftService aircraftService) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.aircraftService = aircraftService;
    }

    public List<Flight> findAll() {
        return flightRepository.findAllWithAircraft();
    }

    public Optional<Flight> findById(Long id) {
        if (id == null) return Optional.empty();
        return flightRepository.findByIdWithAircraft(id);
    }

    @Transactional
    public void save(Flight flight) {
        save(flight, false, null);
    }

    @Transactional
    public void save(Flight flight, boolean systemUpdate, Flight.Status previousStatus) {

        if (flight == null)
            throw new IllegalArgumentException("Рейс не может быть null");

        if (flight.getStatus() == null) {
            flight.setStatus(Flight.Status.SCHEDULED);
        }

        boolean isNew = (flight.getId() == null || flight.getId() == 0);
        Flight existingFlight = null;

        if (!isNew) {
            if (!systemUpdate) {
                existingFlight = flightRepository.findByIdWithAircraft(flight.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));

                if (existingFlight.getStatus() == Flight.Status.ARRIVED ||
                        existingFlight.getStatus() == Flight.Status.DEPARTED) {
                    throw new IllegalStateException("Нельзя изменять завершённые рейсы!");
                }
            } else {
                existingFlight = null;
            }
        }

        validateFlightTimes(flight);
        handleAircraftHours(flight, existingFlight, systemUpdate, previousStatus);

        try {
            flightRepository.save(flight);
            log.info("Сохранён рейс {} (ID={})", flight.getFlightNo(), flight.getId());
        } catch (DataIntegrityViolationException dive) {
            log.error("Ошибка целостности данных при сохранении рейса {}: {}", flight.getFlightNo(), dive.getMessage(), dive);
            throw dive;
        } catch (Exception e) {
            log.error("Ошибка сохранения рейса {}: {}", flight.getFlightNo(), e.getMessage(), e);
            throw new IllegalStateException("Произошла ошибка при сохранении рейса.", e);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("ID рейса не может быть пустым");

        Flight flight = flightRepository.findByIdWithAircraft(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));

        if (flight.getStatus() == Flight.Status.DEPARTED) {
            throw new IllegalStateException("Нельзя удалить рейс, который находится в полёте!");
        }

        adjustAircraftHoursOnDelete(flight);

        try {
            flightRepository.delete(flight);
            log.info("Рейс {} (ID={}) успешно удалён", flight.getFlightNo(), flight.getId());
        } catch (DataIntegrityViolationException dive) {
            log.error("Ошибка удаления рейса {} (ID={}): целостность данных: {}", flight.getFlightNo(), flight.getId(), dive.getMessage(), dive);
            throw dive;
        } catch (Exception e) {
            log.error("Ошибка удаления рейса {} (ID={}): {}", flight.getFlightNo(), flight.getId(), e.getMessage(), e);
            throw new IllegalStateException("Произошла ошибка при удалении рейса.", e);
        }
    }

    private void validateFlightTimes(Flight flight) {
        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null)
            return;

        if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture()))
            throw new IllegalArgumentException("Дата прибытия не может быть раньше вылета!");

        Duration duration = Duration.between(
                flight.getScheduledDeparture(),
                flight.getScheduledArrival()
        );

        flight.setRouteDuration(
                duration.toHours() + " ч " + duration.toMinutesPart() + " мин"
        );
    }

    private void handleAircraftHours(Flight flight, Flight existingFlight, boolean systemUpdate, Flight.Status previousStatus) {

        if (flight.getAircraft() == null) return;

        Aircraft aircraft = aircraftRepository.findById(
                flight.getAircraft().getAircraftCode()
        ).orElseThrow(() -> new IllegalArgumentException("Самолёт не найден!"));

        double currentHours = aircraft.getTotalFlightHours() == null
                ? 0 : aircraft.getTotalFlightHours();

        double flightHours = calculateFlightHours(flight);

        boolean changed = false;

        if (systemUpdate &&
                flight.getStatus() == Flight.Status.ARRIVED &&
                (previousStatus == null || previousStatus != Flight.Status.ARRIVED)) {

            aircraft.setTotalFlightHours(currentHours + flightHours);
            changed = true;

            log.info("SYSTEM UPDATE: Налёт {} увеличен на {} → {}",
                    aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
        }

        if (!systemUpdate &&
                existingFlight != null &&
                existingFlight.getStatus() == Flight.Status.ARRIVED &&
                flight.getStatus() != Flight.Status.ARRIVED) {

            aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));
            changed = true;

            log.info("Налёт {} уменьшен на {} → {} (ручное изменение статуса)",
                    aircraft.getAircraftCode(), flightHours, aircraft.getTotalFlightHours());
        }

        if (changed) {
            try {
                aircraftRepository.save(aircraft);
            } catch (Exception e) {
                log.error("Не удалось сохранить самолёт {}: {}", aircraft.getAircraftCode(), e.getMessage(), e);
                throw e;
            }
        }

        try {
            aircraftService.applyMaintenanceIfNeeded(
                    aircraft,
                    thresholdHours,
                    periodDays
            );
        } catch (Exception e) {
            log.error("applyMaintenanceIfNeeded failed for {}: {}", aircraft.getAircraftCode(), e.getMessage(), e);
            throw e;
        }

        flight.setAircraft(aircraft);
    }

    private void adjustAircraftHoursOnDelete(Flight flight) {
        if (flight.getAircraft() == null) return;

        if (flight.getStatus() == Flight.Status.ARRIVED) {

            Aircraft aircraft = flight.getAircraft();

            double currentHours = aircraft.getTotalFlightHours() == null
                    ? 0 : aircraft.getTotalFlightHours();

            double flightHours = calculateFlightHours(flight);

            aircraft.setTotalFlightHours(Math.max(0, currentHours - flightHours));

            try {
                aircraftRepository.save(aircraft);
                log.info("Удаление рейса уменьшило налёт {} → {} ч", aircraft.getAircraftCode(), aircraft.getTotalFlightHours());
            } catch (Exception e) {
                log.error("Не удалось сохранить самолёт при удалении рейса {}: {}", aircraft.getAircraftCode(), e.getMessage(), e);
                throw e;
            }
        }
    }

    private double calculateFlightHours(Flight flight) {
        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null)
            return 0;

        Duration dur = Duration.between(
                flight.getScheduledDeparture(),
                flight.getScheduledArrival()
        );

        return Math.round((dur.toMinutes() / 60.0) * 100.0) / 100.0;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void processScheduledUpdateForFlight(Flight flight, LocalDateTime now, long onTimeWindowHours) {
        if (flight == null) return;

        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null) return;
        if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) return;

        Flight.Status previous = flight.getStatus();
        Flight.Status newStatus = previous;

        if (now.isAfter(flight.getScheduledArrival())) {
            newStatus = Flight.Status.ARRIVED;
        } else if (now.isAfter(flight.getScheduledDeparture())) {
            newStatus = Flight.Status.DEPARTED;
        } else {
            Duration until = Duration.between(now, flight.getScheduledDeparture());
            if (!until.isNegative() &&
                    until.toHours() <= onTimeWindowHours &&
                    (previous == Flight.Status.SCHEDULED || previous == Flight.Status.DELAYED)) {
                newStatus = Flight.Status.ON_TIME;
            }
        }

        if (newStatus != previous) {
            flight.setStatus(newStatus);
            save(flight, true, previous);
            log.info("AUTO UPDATE: ID={} статус {} → {}", flight.getId(), previous, newStatus);
        }
    }
}
