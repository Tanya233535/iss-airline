package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.repository.AircraftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public List<Aircraft> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    public Aircraft getAircraftByCode(String code) {
        if (code == null || code.isBlank()) {
            log.warn("Попытка поиска самолёта с пустым кодом");
            return null;
        }
        return aircraftRepository.findById(code).orElse(null);
    }

    public Aircraft saveAircraft(Aircraft aircraft) {

        if (aircraft == null) {
            throw new IllegalArgumentException("Самолёт не может быть null");
        }

        if (aircraft.getAircraftCode() == null || aircraft.getAircraftCode().isBlank()) {
            throw new IllegalArgumentException("Код самолёта не может быть пустым");
        }

        if (!aircraftRepository.existsById(aircraft.getAircraftCode())) {
            log.info("Создание нового самолёта {}", aircraft.getAircraftCode());
        } else {
            log.info("Обновление существующего самолёта {}", aircraft.getAircraftCode());
        }

        if (aircraft.getManufactureYear() != null) {
            int year = aircraft.getManufactureYear();
            int currentYear = LocalDate.now().getYear();

            if (year < 1903 || year > currentYear) {
                throw new IllegalArgumentException("Год выпуска должен быть между 1903 и " + currentYear);
            }
        }

        if (aircraft.getLastMaintenanceDate() != null && aircraft.getManufactureYear() != null) {

            if (aircraft.getLastMaintenanceDate().isBefore(
                    LocalDate.of(aircraft.getManufactureYear(), 1, 1)
            )) {
                throw new IllegalArgumentException("Дата последнего ТО не может быть раньше года выпуска!");
            }

            if (aircraft.getLastMaintenanceDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Дата последнего ТО не может быть в будущем!");
            }
        }

        try {
            return aircraftRepository.save(aircraft);

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка БД при сохранении самолёта {}: {}", aircraft.getAircraftCode(), e.getMessage());
            throw new IllegalStateException("Ошибка сохранения данных: проверьте корректность заполнения");
        } catch (Exception e) {
            log.error("Неизвестная ошибка при сохранении самолёта", e);
            throw new IllegalStateException("Не удалось сохранить самолёт");
        }
    }

    public Optional<Aircraft> findById(String id) {
        return aircraftRepository.findById(id);
    }

    public void deleteAircraft(String code) {
        if (!aircraftRepository.existsById(code)) {
            throw new IllegalArgumentException("Самолёт не найден");
        }

        try {
            aircraftRepository.deleteById(code);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Самолёт связан с рейсами и не может быть удалён");
        }
    }
}
