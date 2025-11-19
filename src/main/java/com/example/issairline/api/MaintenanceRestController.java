package com.example.issairline.api;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Maintenance;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.service.MaintenanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceRestController {

    private final MaintenanceService maintenanceService;
    private final AircraftRepository aircraftRepository;

    @GetMapping
    public List<Maintenance> getAll() {
        return maintenanceService.findAll();
    }

    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable Long id) {
        return maintenanceService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись ТО не найдена"));
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance m) {

        Aircraft aircraft = aircraftRepository.findById(m.getAircraft().getAircraftCode())
                .orElseThrow(() -> new EntityNotFoundException("Самолёт не найден"));

        m.setAircraft(aircraft);

        validateType(m.getType());

        maintenanceService.save(m);
        return m;
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance updates) {

        Maintenance existing = maintenanceService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись ТО не найдена"));


        existing.setMaintenanceDate(updates.getMaintenanceDate());
        existing.setNextDueDate(updates.getNextDueDate());
        existing.setEngineerName(updates.getEngineerName());
        existing.setDescription(updates.getDescription());
        existing.setStatus(updates.getStatus());

        validateType(updates.getType());
        existing.setType(updates.getType());

        if (updates.getAircraft() != null) {
            Aircraft aircraft = aircraftRepository.findById(updates.getAircraft().getAircraftCode())
                    .orElseThrow(() -> new EntityNotFoundException("Самолёт не найден"));
            existing.setAircraft(aircraft);
        }

        maintenanceService.save(existing);
        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (maintenanceService.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Запись ТО не найдена");
        }
        maintenanceService.delete(id);
    }

    private void validateType(String type) {
        if (!type.matches("A-check|B-check|C-check|D-check")) {
            throw new IllegalArgumentException("Некорректный тип проверки. Допустимые: A-check, B-check, C-check, D-check");
        }
    }
}
