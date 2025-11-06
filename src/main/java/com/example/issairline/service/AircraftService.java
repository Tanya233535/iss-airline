package com.example.issairline.service;

import com.example.issairline.entity.Aircraft;
import com.example.issairline.repository.AircraftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный слой — содержит бизнес-логику для работы с самолётами.
 */
@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    /**
     * Получить список всех самолётов.
     */
    public List<Aircraft> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    /**
     * Получить самолёт по коду (ID).
     */
    public Optional<Aircraft> getAircraftByCode(String code) {
        return aircraftRepository.findById(code);
    }

    /**
     * Добавить или обновить самолёт.
     */
    public Aircraft saveAircraft(Aircraft aircraft) {
        return aircraftRepository.save(aircraft);
    }

    /**
     * Удалить самолёт по коду.
     */
    public void deleteAircraft(String code) {
        aircraftRepository.deleteById(code);
    }
}
