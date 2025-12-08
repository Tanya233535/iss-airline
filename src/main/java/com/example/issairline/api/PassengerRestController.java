package com.example.issairline.api;

import com.example.issairline.api.dto.PassengerDto;
import com.example.issairline.api.mapper.PassengerMapper;
import com.example.issairline.entity.Flight;
import com.example.issairline.entity.Passenger;
import com.example.issairline.repository.FlightRepository;
import com.example.issairline.service.PassengerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerRestController {

    private final PassengerService passengerService;
    private final FlightRepository flightRepository;

    @GetMapping
    public List<PassengerDto> getAll() {
        return passengerService.findAll().stream()
                .map(PassengerMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public PassengerDto getById(@PathVariable Long id) {
        Passenger p = passengerService.findById(id);
        if (p == null) throw new EntityNotFoundException("Пассажир не найден");
        return PassengerMapper.toDto(p);
    }

    @PostMapping
    public PassengerDto create(@RequestBody PassengerDto dto) {
        Flight flight = null;
        if (dto.getFlightId() != null) {
            flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new EntityNotFoundException("Рейс ID=" + dto.getFlightId() + " не найден"));
        }

        Passenger p = PassengerMapper.toEntity(dto, flight);
        passengerService.save(p);
        return PassengerMapper.toDto(p);
    }

    @PutMapping("/{id}")
    public PassengerDto update(@PathVariable Long id, @RequestBody PassengerDto dto) {
        Passenger existing = passengerService.findById(id);
        if (existing == null) throw new EntityNotFoundException("Пассажир не найден");

        Flight flight = null;
        if (dto.getFlightId() != null) {
            flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new EntityNotFoundException("Рейс ID=" + dto.getFlightId() + " не найден"));
        }

        Passenger updated = PassengerMapper.toEntity(dto, flight);
        updated.setId(id);
        passengerService.save(updated);
        return PassengerMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        passengerService.delete(id);
    }
}
