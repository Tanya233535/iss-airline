package com.example.issairline.service;

import com.example.issairline.entity.Passenger;
import com.example.issairline.repository.PassengerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        try {
            passengerRepository.save(passenger);
            log.info("Сохранён пассажир ID={}", passenger.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Ошибка сохранения пассажира — некорректные данные.");
        } catch (Exception e) {
            log.error("Ошибка сохранения пассажира", e);
            throw new IllegalStateException("Не удалось сохранить пассажира.");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!passengerRepository.existsById(id)) {
            throw new EntityNotFoundException("Пассажир не найден!");
        }

        try {
            passengerRepository.deleteById(id);
            log.info("Удалён пассажир ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка удаления пассажира", e);
            throw new IllegalStateException("Не удалось удалить пассажира.");
        }
    }
}
