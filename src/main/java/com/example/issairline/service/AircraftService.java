package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.repository.AircraftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой — содержит бизнес-логику для работы с самолётами.
 */
@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public List<Aircraft> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    public Optional<Aircraft> getAircraftByCode(String code) {
        return aircraftRepository.findById(code);
    }

    public Aircraft saveAircraft(Aircraft aircraft) {
        if (aircraft.getManufactureYear() != null) {
            int year = aircraft.getManufactureYear();
            int currentYear = java.time.LocalDate.now().getYear();
            if (year < 1903 || year > currentYear) {
                throw new IllegalArgumentException("Год выпуска должен быть между 1903 и " + currentYear);
            }
        }

        if (aircraft.getLastMaintenanceDate() != null && aircraft.getManufactureYear() != null) {
            int maintenanceYear = aircraft.getLastMaintenanceDate().getYear();
            if (maintenanceYear < aircraft.getManufactureYear()) {
                throw new IllegalArgumentException("Дата последнего ТО не может быть раньше года выпуска!");
            }
            if (aircraft.getLastMaintenanceDate().isAfter(java.time.LocalDate.now())) {
                throw new IllegalArgumentException("Дата последнего ТО не может быть в будущем!");
            }
        }

        return aircraftRepository.save(aircraft);
    }

    public Optional<Aircraft> findById(String id) {
        return aircraftRepository.findById(id);
    }

    public void deleteAircraft(String code) {
        aircraftRepository.deleteById(code);
    }
}
