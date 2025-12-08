package com.example.issairline.api;

import com.example.issairline.api.dto.UserDto;
import com.example.issairline.api.mapper.UserMapper;
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
    public List<UserDto> getAll() {
        return userService.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) throw new EntityNotFoundException("Пользователь не найден");
        return UserMapper.toDto(user);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        if (userService.existsUsername(dto.getUsername())) {
            throw new IllegalStateException("Логин уже существует");
        }
        User u = UserMapper.toEntity(dto);
        userService.save(u, true);
        return UserMapper.toDto(u);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        User existing = userService.findById(id);
        if (existing == null) throw new EntityNotFoundException("Пользователь не найден");

        if (!existing.getUsername().equals(dto.getUsername()) &&
                userService.existsUsername(dto.getUsername())) {
            throw new IllegalStateException("Логин уже занят другим пользователем");
        }

        existing.setUsername(dto.getUsername());
        existing.setRole(User.Role.valueOf(dto.getRole()));
        userService.save(existing, false);
        return UserMapper.toDto(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) throw new EntityNotFoundException("Пользователь не найден");
        userService.delete(id);
    }
}
