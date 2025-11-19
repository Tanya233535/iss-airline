package com.example.issairline.service;

import com.example.issairline.entity.CrewMember;
import com.example.issairline.repository.CrewMemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CrewMemberService {

    private final CrewMemberRepository crewRepository;

    public CrewMemberService(CrewMemberRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    public List<CrewMember> findAll() {
        return crewRepository.findAll();
    }

    public Optional<CrewMember> findById(Long id) {
        if (id == null) {
            log.warn("Передан null вместо ID члена экипажа");
            return Optional.empty();
        }
        return crewRepository.findById(id);
    }

    public List<CrewMember> findByFlight(Long flightId) {
        if (flightId == null) {
            log.warn("Попытка получить экипаж для null flightId");
            return List.of();
        }
        return crewRepository.findByFlight_Id(flightId);
    }

    @Transactional
    public void save(CrewMember member) {

        if (member == null) {
            throw new IllegalArgumentException("Член экипажа не может быть null");
        }

        if (member.getFirstName() == null || member.getLastName() == null) {
            throw new IllegalArgumentException("Имя и фамилия не могут быть пустыми");
        }

        try {
            crewRepository.save(member);

            log.info("Сохранён член экипажа: {} {} (роль: {}, рейс: {})",
                    member.getLastName(),
                    member.getFirstName(),
                    member.getRole(),
                    member.getFlight() != null ? member.getFlight().getId() : "без рейса");

        } catch (DataIntegrityViolationException ex) {

            log.warn("Нарушение ограничений при сохранении члена экипажа {} {} — возможно, дубликат",
                    member.getLastName(), member.getFirstName());

            throw new EntityExistsException("Такой член экипажа уже существует или прикреплён к этому рейсу!");
        } catch (Exception ex) {
            log.error("Ошибка при сохранении члена экипажа", ex);
            throw new IllegalStateException("Не удалось сохранить данные члена экипажа!");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не может быть пустым");
        }

        try {
            if (!crewRepository.existsById(id)) {
                throw new EntityNotFoundException("Член экипажа не найден!");
            }

            crewRepository.deleteById(id);
            log.info("Удалён член экипажа ID={}", id);

        } catch (EntityNotFoundException e) {
            log.warn("Попытка удалить несуществующего члена экипажа ID={}", id);
            throw e;

        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка удаления — член экипажа связан с данными", e);
            throw new IllegalStateException("Невозможно удалить: член экипажа связан с рейсом");

        } catch (Exception e) {
            log.error("Ошибка при удалении члена экипажа", e);
            throw new IllegalStateException("Не удалось удалить члена экипажа!");
        }
    }
}
