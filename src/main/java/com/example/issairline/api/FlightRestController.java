package com.example.issairline.api;

import com.example.issairline.api.dto.FlightDto;
import com.example.issairline.api.mapper.FlightMapper;
import com.example.issairline.entity.Aircraft;
import com.example.issairline.entity.Flight;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightRestController {

    private final FlightService flightService;
    private final AircraftRepository aircraftRepository;

    @GetMapping
    public List<FlightDto> getAll() {
        return flightService.findAll().stream()
                .map(FlightMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public FlightDto getById(@PathVariable Long id) {
        Flight flight = flightService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
        return FlightMapper.toDto(flight);
    }

    @PostMapping
    public FlightDto create(@RequestBody FlightDto dto) {
        if (dto.getAircraftCode() == null || dto.getAircraftCode().isBlank()) {
            throw new IllegalArgumentException("Не указан aircraftCode");
        }

        Aircraft aircraft = aircraftRepository.findById(dto.getAircraftCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Самолёт с кодом " + dto.getAircraftCode() + " не найден!"
                ));

        if (aircraft.getStatus() == Aircraft.Status.MAINTENANCE) {
            throw new IllegalStateException("Самолёт " + aircraft.getAircraftCode() + " находится на техническом обслуживании");
        }

        Flight flight = FlightMapper.toEntity(dto, aircraft);
        flightService.save(flight);
        return FlightMapper.toDto(flight);
    }

    @PutMapping("/{id}")
    public FlightDto update(@PathVariable Long id, @RequestBody FlightDto dto) {
        Flight existing = flightService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));

        if (dto.getAircraftCode() == null || dto.getAircraftCode().isBlank()) {
            throw new IllegalArgumentException("Не указан aircraftCode");
        }

        Aircraft aircraft = aircraftRepository.findById(dto.getAircraftCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Самолёт с кодом " + dto.getAircraftCode() + " не найден!"
                ));

        if (aircraft.getStatus() == Aircraft.Status.MAINTENANCE) {
            throw new IllegalStateException("Самолёт " + aircraft.getAircraftCode() + " находится на техническом обслуживании");
        }

        Flight updated = FlightMapper.toEntity(dto, aircraft);
        updated.setId(id);
        flightService.save(updated);
        return FlightMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        flightService.deleteById(id);
    }
}
