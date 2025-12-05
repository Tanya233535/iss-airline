package com.example.issairline.service;

import com.example.issairline.entity.Flight;
import com.example.issairline.repository.FlightRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    public void runUpdateCycle() {
        List<Flight> flights = flightRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Flight f : flights) {
            try {
                flightService.processScheduledUpdateForFlight(f.getId(), now, onTimeWindowHours);
            } catch (Exception e) {
                log.error("Ошибка обработки рейса ID={}: {}", f.getId(), e.getMessage(), e);
            }
        }
    }
}
