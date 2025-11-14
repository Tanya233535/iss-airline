package com.example.issairline.service;

import com.example.issairline.entity.Passenger;
import com.example.issairline.repository.PassengerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public List<Passenger> findAll() {
        return passengerRepository.findAll();
    }

    public Passenger findById(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пассажир не найден!"));
    }

    @Transactional
    public void save(Passenger passenger) {
        passengerRepository.save(passenger);
    }

    @Transactional
    public void delete(Long id) {
        if (!passengerRepository.existsById(id)) {
            throw new EntityNotFoundException("Пассажир не найден!");
        }
        passengerRepository.deleteById(id);
    }
}
