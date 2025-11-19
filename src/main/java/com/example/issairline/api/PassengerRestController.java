package com.example.issairline.api;

import com.example.issairline.entity.Passenger;
import com.example.issairline.entity.Flight;
import com.example.issairline.service.PassengerService;
import com.example.issairline.repository.FlightRepository;
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
    public List<Passenger> getAll() {
        return passengerService.findAll();
    }

    @GetMapping("/{id}")
    public Passenger getById(@PathVariable Long id) {
        return passengerService.findById(id);
    }

    @PostMapping
    public Passenger create(@RequestBody Passenger passenger) {
        attachFlight(passenger);
        passengerService.save(passenger);
        return passenger;
    }

    @PutMapping("/{id}")
    public Passenger update(@PathVariable Long id, @RequestBody Passenger updates) {

        Passenger existing = passengerService.findById(id);

        existing.setFirstName(updates.getFirstName());
        existing.setLastName(updates.getLastName());
        existing.setMiddleName(updates.getMiddleName());
        existing.setPassportNumber(updates.getPassportNumber());
        existing.setTicketNumber(updates.getTicketNumber());
        existing.setSeat(updates.getSeat());

        if (updates.getFlight() != null) {
            Flight flight = getFlight(updates.getFlight().getId());
            existing.setFlight(flight);
        } else {
            existing.setFlight(null);
        }

        passengerService.save(existing);
        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        passengerService.delete(id);
    }

    private void attachFlight(Passenger passenger) {
        if (passenger.getFlight() != null) {
            Long flightId = passenger.getFlight().getId();
            Flight flight = getFlight(flightId);
            passenger.setFlight(flight);
        }
    }

    private Flight getFlight(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Рейс ID=" + id + " не найден"));
    }
}
