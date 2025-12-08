package com.example.issairline.api;

import com.example.issairline.api.dto.CrewMemberDto;
import com.example.issairline.api.mapper.CrewMemberMapper;
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
    public List<CrewMemberDto> getAll() {
        return crewService.findAll().stream()
                .map(CrewMemberMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CrewMemberDto getById(@PathVariable Long id) {
        CrewMember member = crewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Член экипажа не найден"));
        return CrewMemberMapper.toDto(member);
    }

    @PostMapping
    public CrewMemberDto create(@RequestBody CrewMemberDto dto) {
        Flight flight = null;
        if (dto.getFlightId() != null) {
            flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new EntityNotFoundException("Рейс ID=" + dto.getFlightId() + " не найден"));
        }
        CrewMember member = CrewMemberMapper.toEntity(dto, flight);
        try {
            crewService.save(member);
        } catch (EntityExistsException e) {
            throw new EntityExistsException("Такой член экипажа уже существует для этого рейса!");
        }
        return CrewMemberMapper.toDto(member);
    }

    @PutMapping("/{id}")
    public CrewMemberDto update(@PathVariable Long id, @RequestBody CrewMemberDto dto) {
        CrewMember existing = crewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Член экипажа не найден"));

        Flight flight = null;
        if (dto.getFlightId() != null) {
            flight = flightRepository.findById(dto.getFlightId())
                    .orElseThrow(() -> new EntityNotFoundException("Рейс ID=" + dto.getFlightId() + " не найден"));
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setMiddleName(dto.getMiddleName());
        existing.setRole(dto.getRole());
        existing.setQualification(dto.getQualification());
        existing.setExperienceYears(dto.getExperienceYears());
        existing.setFlight(flight);

        try {
            crewService.save(existing);
        } catch (EntityExistsException e) {
            throw new EntityExistsException("Такой член экипажа уже существует для этого рейса!");
        }
        return CrewMemberMapper.toDto(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        crewService.deleteById(id);
    }
}
