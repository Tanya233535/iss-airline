package com.example.issairline.api;

import com.example.issairline.api.dto.AircraftDto;
import com.example.issairline.api.mapper.AircraftMapper;
import com.example.issairline.entity.Aircraft;
import com.example.issairline.service.AircraftService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircrafts")
@RequiredArgsConstructor
public class AircraftRestController {

    private final AircraftService service;

    @GetMapping
    public List<AircraftDto> getAll() {
        return service.getAllAircrafts().stream()
                .map(AircraftMapper::toDto)
                .toList();
    }

    @GetMapping("/{code}")
    public AircraftDto getOne(@PathVariable String code) {
        Aircraft aircraft = service.getAircraftByCode(code);
        if (aircraft == null) {
            throw new EntityNotFoundException("Самолёт с кодом " + code + " не найден");
        }
        return AircraftMapper.toDto(aircraft);
    }

    @PostMapping
    public AircraftDto create(@RequestBody AircraftDto dto) {
        if (service.getAircraftByCode(dto.getAircraftCode()) != null) {
            throw new EntityExistsException("Самолёт с кодом " + dto.getAircraftCode() + " уже существует");
        }
        Aircraft aircraft = AircraftMapper.toEntity(dto);
        service.saveAircraft(aircraft);
        return AircraftMapper.toDto(aircraft);
    }

    @PutMapping("/{code}")
    public AircraftDto update(@PathVariable String code, @RequestBody AircraftDto dto) {
        Aircraft existing = service.getAircraftByCode(code);
        if (existing == null) {
            throw new EntityNotFoundException("Самолёт для обновления не найден: " + code);
        }
        AircraftMapper.toEntity(dto).setAircraftCode(code);
        existing.setModel(dto.getModel());
        existing.setManufactureYear(dto.getManufactureYear());
        existing.setCapacity(dto.getCapacity());
        existing.setStatus(Aircraft.Status.valueOf(dto.getStatus()));
        existing.setLastMaintenanceDate(dto.getLastMaintenanceDate() == null ? null : java.time.LocalDate.parse(dto.getLastMaintenanceDate()));
        existing.setTotalFlightHours(dto.getTotalFlightHours());
        service.saveAircraft(existing);
        return AircraftMapper.toDto(existing);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {
        Aircraft existing = service.getAircraftByCode(code);
        if (existing == null) throw new EntityNotFoundException("Самолёт не найден: " + code);
        service.deleteAircraft(code);
    }
}
