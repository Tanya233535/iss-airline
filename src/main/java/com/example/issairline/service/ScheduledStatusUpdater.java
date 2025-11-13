package com.example.issairline.service;

import com.example.issairline.entity.Flight;
import com.example.issairline.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Планировщик, который регулярно обновляет статусы рейсов.
 */
@Component
public class ScheduledStatusUpdater {

    private static final Logger log = LoggerFactory.getLogger(ScheduledStatusUpdater.class);

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

    /**
     * Выполняется каждые N миллисекунд (по умолчанию — раз в минуту).
     */
    @Scheduled(fixedDelayString = "${flights.status.update-interval-ms:60000}")
    public void scheduledUpdate() {
        try {
            runUpdateCycle();
        } catch (Exception e) {
            log.error("Ошибка при выполнении обновления статусов рейсов: ", e);
        }
    }

    @Transactional
    public void runUpdateCycle() {
        LocalDateTime now = LocalDateTime.now();
        List<Flight> flights = flightRepository.findAll();

        for (Flight flight : flights) {
            try {
                updateFlightStatus(flight, now);
            } catch (Exception e) {
                log.error("Ошибка обработки рейса id={}: {}", flight.getId(), e.getMessage());
            }
        }
    }

    /**
     * Обновляет статус конкретного рейса в зависимости от времени.
     */
    private void updateFlightStatus(Flight flight, LocalDateTime now) {
        if (flight.getScheduledDeparture() == null || flight.getScheduledArrival() == null) return;

        if (flight.getScheduledArrival().isBefore(flight.getScheduledDeparture())) {
            log.warn("Рейс id={} имеет некорректные даты (прибытие раньше вылета) — пропущен", flight.getId());
            return;
        }

        Flight.Status current = flight.getStatus();

        // ARRIVED — если текущее время >= запланированного прибытия
        if (now.isAfter(flight.getScheduledArrival()) || now.isEqual(flight.getScheduledArrival())) {
            if (current != Flight.Status.ARRIVED) {
                flight.setStatus(Flight.Status.ARRIVED);
                flightService.save(flight, true); // системное обновление
                log.info("Рейс id={} обновлён: {} → ARRIVED", flight.getId(), current);
            }
            return;
        }

        // DEPARTED — если вылет уже начался, но рейс ещё не прибыл
        if (now.isAfter(flight.getScheduledDeparture()) && now.isBefore(flight.getScheduledArrival())) {
            if (current != Flight.Status.DEPARTED) {
                flight.setStatus(Flight.Status.DEPARTED);
                flightService.save(flight, true); // системное обновление
                log.info("Рейс id={} обновлён: {} → DEPARTED", flight.getId(), current);
            }
            return;
        }

        // ON_TIME — если до вылета осталось меньше onTimeWindowHours
        Duration untilDeparture = Duration.between(now, flight.getScheduledDeparture());
        if ((current == Flight.Status.SCHEDULED || current == Flight.Status.DELAYED)
                && !untilDeparture.isNegative()
                && untilDeparture.toHours() <= onTimeWindowHours) {
            if (current != Flight.Status.ON_TIME) {
                flight.setStatus(Flight.Status.ON_TIME);
                flightService.save(flight, true);
                log.info("Рейс id={} обновлён: {} → ON_TIME", flight.getId(), current);
            }
        }
    }
}
