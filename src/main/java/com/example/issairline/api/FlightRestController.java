package com.example.issairline.api;

import com.example.issairline.entity.Flight;
import com.example.issairline.entity.Aircraft;
import com.example.issairline.repository.AircraftRepository;
import com.example.issairline.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightRestController {

    private final FlightService flightService;
    private final AircraftRepository aircraftRepository;

    @GetMapping
    public List<Flight> getAll() {
        return flightService.findAll();
    }

    @GetMapping("/{id}")
    public Flight getById(@PathVariable Long id) {
        return flightService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
    }

    @PostMapping
    public Flight create(@RequestBody Flight flight) {

        Aircraft aircraft = loadAircraft(flight);
        flight.setAircraft(aircraft);

        flightService.save(flight);
        return flight;
    }

    @PutMapping("/{id}")
    public Flight update(@PathVariable Long id, @RequestBody Flight updates) {

        Flight existing = flightService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден!"));

        existing.setFlightNo(updates.getFlightNo());
        existing.setScheduledDeparture(updates.getScheduledDeparture());
        existing.setScheduledArrival(updates.getScheduledArrival());
        existing.setDepartureAirport(updates.getDepartureAirport());
        existing.setArrivalAirport(updates.getArrivalAirport());
        existing.setActualDeparture(updates.getActualDeparture());
        existing.setActualArrival(updates.getActualArrival());
        existing.setStatus(updates.getStatus());

        Aircraft aircraft = loadAircraft(updates);
        existing.setAircraft(aircraft);

        flightService.save(existing);
        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        flightService.deleteById(id);
    }

    private Aircraft loadAircraft(Flight flight) {
        if (flight.getAircraft() == null) {
            throw new EntityNotFoundException("Самолёт обязателен (aircraft_code)");
        }

        String code = flight.getAircraft().getAircraftCode();

        return aircraftRepository.findById(code)
                .orElseThrow(() ->
                        new EntityNotFoundException("Самолёт с кодом " + code + " не найден!"));
    }
}
