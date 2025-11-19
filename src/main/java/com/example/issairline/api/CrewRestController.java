package com.example.issairline.api;

import com.example.issairline.entity.CrewMember;
import com.example.issairline.entity.Flight;
import com.example.issairline.service.CrewMemberService;
import com.example.issairline.repository.FlightRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crew")
@RequiredArgsConstructor
public class CrewRestController {

    private final CrewMemberService crewService;
    private final FlightRepository flightRepository;

    @GetMapping
    public List<CrewMember> getAll() {
        return crewService.findAll();
    }

    @GetMapping("/{id}")
    public CrewMember getById(@PathVariable Long id) {
        return crewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Член экипажа не найден"));
    }

    @PostMapping
    public CrewMember create(@RequestBody CrewMember member) {

        attachFlightIfNeeded(member);

        try {
            crewService.save(member);
        } catch (EntityExistsException e) {
            throw new EntityExistsException("Такой член экипажа уже существует для этого рейса!");
        }

        return member;
    }

    @PutMapping("/{id}")
    public CrewMember update(@PathVariable Long id, @RequestBody CrewMember updates) {

        CrewMember existing = crewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Член экипажа не найден"));

        existing.setFirstName(updates.getFirstName());
        existing.setLastName(updates.getLastName());
        existing.setMiddleName(updates.getMiddleName());
        existing.setRole(updates.getRole());
        existing.setQualification(updates.getQualification());
        existing.setExperienceYears(updates.getExperienceYears());

        if (updates.getFlight() != null) {
            Flight flight = getFlightOrThrow(updates.getFlight().getId());
            existing.setFlight(flight);
        } else {
            existing.setFlight(null);
        }

        try {
            crewService.save(existing);
        } catch (EntityExistsException e) {
            throw new EntityExistsException("Такой член экипажа уже существует для этого рейса!");
        }

        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        crewService.deleteById(id);
    }

    private void attachFlightIfNeeded(CrewMember member) {
        if (member.getFlight() != null) {
            Long flightId = member.getFlight().getId();
            Flight flight = getFlightOrThrow(flightId);
            member.setFlight(flight);
        }
    }

    private Flight getFlightOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Рейс ID=" + id + " не найден"));
    }
}
