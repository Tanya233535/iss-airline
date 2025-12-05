package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Maintenance;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.repository.MaintenanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final MaintenanceRepository repo;
    private final AircraftRepository aircraftRepository;

    public List<Maintenance> findAll() {
        return repo.findAll();
    }

    public Optional<Maintenance> findById(Long id) {
        if (id == null) {
            log.warn("findById(null) — неверный ID");
            return Optional.empty();
        }
        return repo.findById(id);
    }

    @Transactional
    public void save(Maintenance m) {
        try {
            repo.save(m);

            Aircraft ac = m.getAircraft();
            if (ac != null) {

                LocalDate maintenanceDate = m.getMaintenanceDate() != null
                        ? m.getMaintenanceDate()
                        : LocalDate.now();

                ac.setLastMaintenanceDate(maintenanceDate);

                ac.setStatus(Aircraft.Status.ACTIVE);

                aircraftRepository.save(ac);

                log.info(
                        "ТО завершено: самолёт {} получил дату ТО {} и статус {}",
                        ac.getAircraftCode(),
                        ac.getLastMaintenanceDate(),
                        ac.getStatus()
                );
            }

            log.info("Сохранена запись ТО ID={}", m.getId());

        } catch (DataIntegrityViolationException e) {
            log.warn("Ошибка целостности БД при сохранении ТО");
            throw new IllegalStateException("Не удаётся сохранить запись техобслуживания");
        } catch (Exception e) {
            log.error("Ошибка сохранения ТО", e);
            throw new IllegalStateException("Произошла ошибка при сохранении записи ТО");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Запись ТО не найдена!");
        }

        try {
            repo.deleteById(id);
            log.info("Удалена запись ТО ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении ТО", e);
            throw new IllegalStateException("Не удалось удалить запись ТО");
        }
    }
}
