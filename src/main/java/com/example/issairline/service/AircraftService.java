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

    public boolean needsMaintenance(Aircraft ac, long thresholdHours, long periodDays) {
        if (ac == null) return false;

        double hours = ac.getTotalFlightHours() == null ? 0 : ac.getTotalFlightHours();

        if (hours >= thresholdHours) {
            return true;
        }

        if (ac.getLastMaintenanceDate() != null) {
            if (ac.getLastMaintenanceDate().plusDays(periodDays).isBefore(LocalDate.now())) {
                return true;
            }
        }

        return false;
    }

    public MaintenanceType detectRequiredCheck(Aircraft ac) {
        if (ac == null) return null;

        double hours = ac.getTotalFlightHours() == null ? 0 : ac.getTotalFlightHours();
        LocalDate last = ac.getLastMaintenanceDate();

        if (hours >= MaintenanceType.A_CHECK.flightHoursInterval) {
            return MaintenanceType.A_CHECK;
        }

        if (last != null && last.plusDays(MaintenanceType.B_CHECK.daysInterval)
                .isBefore(LocalDate.now())) {
            return MaintenanceType.B_CHECK;
        }

        if (hours >= MaintenanceType.C_CHECK.flightHoursInterval) {
            return MaintenanceType.C_CHECK;
        }

        if (last != null && last.plusDays(MaintenanceType.C_CHECK.daysInterval)
                .isBefore(LocalDate.now())) {
            return MaintenanceType.C_CHECK;
        }

        if (last != null && last.plusDays(MaintenanceType.D_CHECK.daysInterval)
                .isBefore(LocalDate.now())) {
            return MaintenanceType.D_CHECK;
        }

        return null;
    }

    public void applyMaintenanceIfNeeded(Aircraft ac,
                                         long thresholdHours,
                                         long periodDays) {
        if (ac == null) return;

        if (needsMaintenance(ac, thresholdHours, periodDays)) {
            if (ac.getStatus() != Aircraft.Status.MAINTENANCE) {
                log.warn("Самолёт {} превышает срок простого ТО → MAINTENANCE",
                        ac.getAircraftCode());
                ac.setStatus(Aircraft.Status.MAINTENANCE);
                aircraftRepository.save(ac);
            }
        }

        MaintenanceType required = detectRequiredCheck(ac);

        if (required != null) {
            log.warn("Самолёту {} требуется {}", ac.getAircraftCode(), required);
            if (ac.getStatus() != Aircraft.Status.MAINTENANCE) {
                ac.setStatus(Aircraft.Status.MAINTENANCE);
                aircraftRepository.save(ac);
                log.warn("Самолёт {} переведён в MAINTENANCE из-за {}", ac.getAircraftCode(), required);
            }
        }
    }

    public void checkAllAircraftsSafely(long thresholdHours, long periodDays) {
        for (Aircraft ac : aircraftRepository.findAll()) {
            if (ac == null) continue;
            applyMaintenanceIfNeeded(ac, thresholdHours, periodDays);
        }
    }
}
