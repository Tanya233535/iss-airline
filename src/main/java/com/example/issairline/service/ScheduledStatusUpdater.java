package com.example.issairline.service;

import com.example.issairline.entity.Flight;
import com.example.issairline.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ScheduledStatusUpdater {

    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final long onTimeWindowHours;

    public ScheduledStatusUpdater(
            FlightRepository flightRepository,
            FlightService flightService,
            @Value("${flights.status.on-time-window-hours:6}") long onTimeWindowHours
    ) {
        this.flightRepository = flightRepository;
        this.flightService = flightService;
        this.onTimeWindowHours = onTimeWindowHours;
    }

    @Scheduled(fixedDelayString = "${flights.status.update-interval-ms:60000}")
    public void scheduledUpdate() {
        try {
            runUpdateCycle();
        } catch (Exception e) {
            log.error("Ошибка обновления статусов рейсов", e);
        }
    }

    @Transactional
    public void runUpdateCycle() {
        List<Flight> flights = flightRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Flight flight : flights) {
            try {
                updateFlightStatus(flight, now);
            } catch (Exception e) {
                log.error("Ошибка обработки рейса ID={}: {}", flight.getId(), e.getMessage());
            }
        }
    }

    private void updateFlightStatus(Flight flight, LocalDateTime now) {
        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null) {
            log.warn("Рейс ID={} имеет пустые даты — пропуск", flight.getId());
            return;
        }

        if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) {
            log.warn("Рейс ID={} имеет некорректные даты — пропуск", flight.getId());
            return;
        }

        Flight.Status previous = flight.getStatus();
        Flight.Status newStatus = previous;

        if (now.isAfter(flight.getScheduledArrival())) {
            newStatus = Flight.Status.ARRIVED;
        } else if (now.isAfter(flight.getScheduledDeparture())) {
            newStatus = Flight.Status.DEPARTED;
        } else {
            Duration until = Duration.between(now, flight.getScheduledDeparture());
            if ((previous == Flight.Status.SCHEDULED || previous == Flight.Status.DELAYED)
                    && !until.isNegative()
                    && until.toHours() <= onTimeWindowHours) {
                newStatus = Flight.Status.ON_TIME;
            }
        }

        if (newStatus != previous) {
            flight.setStatus(newStatus);
            flightService.save(flight, true);
            log.info("Рейс ID={} статус изменён: {} → {}", flight.getId(), previous, newStatus);
        }
    }
}

