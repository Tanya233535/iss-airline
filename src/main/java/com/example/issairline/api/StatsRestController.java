package com.example.issairline.api;

import com.example.issairline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsRestController {

    private final UserRepository userRepository;

    @GetMapping("/users/count")
    public int getUserCount() {
        return (int) userRepository.count();
    }
}
