package com.example.issairline.service;

import com.example.issairline.entity.User;
import com.example.issairline.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден!"));
    }

    @Transactional
    public void save(User user, boolean encodePassword) {
        if (encodePassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        try {
            repo.save(user);
            log.info("Пользователь {} сохранён (ID={})", user.getUsername(), user.getId());
        } catch (Exception e) {
            log.error("Ошибка сохранения пользователя", e);
            throw new IllegalStateException("Ошибка при сохранении пользователя");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Пользователь не найден!");
        }

        try {
            repo.deleteById(id);
            log.info("Пользователь ID={} удалён", id);
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя", e);
            throw new IllegalStateException("Не удалось удалить пользователя");
        }
    }

    public boolean existsUsername(String username) {
        return repo.existsByUsername(username);
    }
}
