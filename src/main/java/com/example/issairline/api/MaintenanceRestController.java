package com.example.issairline.api;

import com.example.issairline.api.dto.MaintenanceDto;
import com.example.issairline.api.mapper.MaintenanceMapper;
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
    public List<MaintenanceDto> getAll() {
        return maintenanceService.findAll().stream()
                .map(MaintenanceMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public MaintenanceDto getById(@PathVariable Long id) {
        Maintenance m = maintenanceService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись ТО не найдена"));
        return MaintenanceMapper.toDto(m);
    }

    @PostMapping
    public MaintenanceDto create(@RequestBody MaintenanceDto dto) {
        Aircraft aircraft = aircraftRepository.findById(dto.getAircraftCode())
                .orElseThrow(() -> new EntityNotFoundException("Самолёт не найден"));

        Maintenance m = MaintenanceMapper.toEntity(dto, aircraft);
        validateType(m.getType());
        maintenanceService.save(m);
        return MaintenanceMapper.toDto(m);
    }

    @PutMapping("/{id}")
    public MaintenanceDto update(@PathVariable Long id, @RequestBody MaintenanceDto dto) {
        Maintenance existing = maintenanceService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Запись ТО не найдена"));

        Aircraft aircraft = aircraftRepository.findById(dto.getAircraftCode())
                .orElseThrow(() -> new EntityNotFoundException("Самолёт не найден"));

        Maintenance updated = MaintenanceMapper.toEntity(dto, aircraft);
        updated.setId(id);
        validateType(updated.getType());
        maintenanceService.save(updated);
        return MaintenanceMapper.toDto(updated);
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
