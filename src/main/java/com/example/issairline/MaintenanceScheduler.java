package com.example.issairline;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.service.AircraftService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MaintenanceScheduler {

    private final AircraftRepository aircraftRepository;
    private final AircraftService aircraftService;

    private final long thresholdHours;
    private final long periodDays;

    public MaintenanceScheduler(AircraftRepository aircraftRepository,
                                AircraftService aircraftService,
                                @Value("${aircraft.maintenance.threshold-hours}") long thresholdHours,
                                @Value("${aircraft.maintenance.period-days}") long periodDays) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftService = aircraftService;
        this.thresholdHours = thresholdHours;
        this.periodDays = periodDays;
    }

    @Scheduled(fixedDelayString = "${aircraft.maintenance.check-interval-ms}")
    public void checkMaintenance() {

        List<Aircraft> list = aircraftRepository.findAll();

        for (Aircraft ac : list) {
            try {
                aircraftService.applyMaintenanceIfNeeded(ac, thresholdHours, periodDays);
            } catch (Exception e) {
                log.error("Ошибка проверки ТО для {}: {}", ac.getAircraftCode(), e.getMessage());
            }
        }
    }
}

