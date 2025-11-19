package com.example.issairline.api;

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
    public List<Aircraft> getAll() {
        return service.getAllAircrafts();
    }

    @GetMapping("/{code}")
    public Aircraft getOne(@PathVariable String code) {
        Aircraft aircraft = service.getAircraftByCode(code);
        if (aircraft == null) {
            throw new EntityNotFoundException("Самолёт с кодом " + code + " не найден");
        }
        return aircraft;
    }

    @PostMapping
    public Aircraft create(@RequestBody Aircraft aircraft) {

        Aircraft exists = service.getAircraftByCode(aircraft.getAircraftCode());
        if (exists != null) {
            throw new EntityExistsException("Самолёт с кодом " + aircraft.getAircraftCode() + " уже существует");
        }

        return service.saveAircraft(aircraft);
    }

    @PutMapping("/{code}")
    public Aircraft update(@PathVariable String code, @RequestBody Aircraft updates) {

        Aircraft existing = service.getAircraftByCode(code);
        if (existing == null) {
            throw new EntityNotFoundException("Самолёт для обновления не найден: " + code);
        }

        updates.setAircraftCode(code);

        existing.setModel(updates.getModel());
        existing.setManufactureYear(updates.getManufactureYear());
        existing.setCapacity(updates.getCapacity());
        existing.setStatus(updates.getStatus());
        existing.setLastMaintenanceDate(updates.getLastMaintenanceDate());
        existing.setTotalFlightHours(updates.getTotalFlightHours());

        return service.saveAircraft(existing);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code) {

        Aircraft existing = service.getAircraftByCode(code);
        if (existing == null) {
            throw new EntityNotFoundException("Самолёт не найден: " + code);
        }

        service.deleteAircraft(code);
    }
}
