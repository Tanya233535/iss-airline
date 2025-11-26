package com.example.issairline.api;

import com.example.issairline.entity.User;
import com.example.issairline.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null)
            throw new EntityNotFoundException("Пользователь не найден");

        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) {

        if (userService.existsUsername(user.getUsername())) {
            throw new IllegalStateException("Логин уже существует");
        }

        userService.save(user, true);

        return user;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User updates) {

        User existing = userService.findById(id);
        if (existing == null)
            throw new EntityNotFoundException("Пользователь не найден");

        if (!existing.getUsername().equals(updates.getUsername())
                && userService.existsUsername(updates.getUsername())) {
            throw new IllegalStateException("Логин уже занят другим пользователем");
        }

        existing.setUsername(updates.getUsername());
        existing.setRole(updates.getRole());

        if (updates.getPassword() != null && !updates.getPassword().isBlank()) {
            userService.save(existing, true);
        } else {
            userService.save(existing, false);
        }

        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        User user = userService.findById(id);
        if (user == null)
            throw new EntityNotFoundException("Пользователь не найден");

        userService.delete(id);
    }
}
