package com.example.issairline.service;

import com.example.issairline.entity.Maintenance;
import com.example.issairline.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repo;

    public List<Maintenance> findAll() {
        return repo.findAll();
    }

    public Optional<Maintenance> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public void save(Maintenance m) {
        repo.save(m);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
