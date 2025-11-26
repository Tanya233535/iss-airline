package com.example.issairline.api;

import com.example.issairline.api.dto.AuthResponse;
import com.example.issairline.entity.User;
import com.example.issairline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public AuthResponse login(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication failed");
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(user.getUsername(), user.getRole().name());
    }
}
