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
        return crewRepository.findById(id);
    }

    public List<CrewMember> findByFlight(Long flightId) {
        return crewRepository.findByFlight_Id(flightId);
    }

    @Transactional
    public void save(CrewMember member) {
        try {
            crewRepository.save(member);
            log.info("Сохранён член экипажа: {} {} ({})",
                    member.getLastName(), member.getFirstName(), member.getRole());
        } catch (DataIntegrityViolationException ex) {
            log.warn("Попытка добавить дубликат: {} {} ({})", member.getLastName(), member.getFirstName(), member.getRole());
            throw new EntityExistsException("Такой член экипажа уже прикреплён к этому рейсу!");
        } catch (Exception ex) {
            log.error("Ошибка при сохранении члена экипажа: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не удалось сохранить данные члена экипажа!");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            if (!crewRepository.existsById(id)) {
                throw new EntityNotFoundException("Член экипажа не найден!");
            }
            crewRepository.deleteById(id);
            log.info("Удалён член экипажа ID={}", id);
        } catch (EntityNotFoundException e) {
            log.warn("Попытка удалить несуществующего члена экипажа: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении члена экипажа: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить члена экипажа!");
        }
    }
}
