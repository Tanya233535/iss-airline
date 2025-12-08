package com.example.issairline.api.mapper;

import com.example.issairline.api.dto.UserDto;
import com.example.issairline.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());

        return dto;
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;

        User u = new User();
        u.setId(dto.getId());
        u.setUsername(dto.getUsername());
        u.setRole(User.Role.valueOf(dto.getRole()));
        u.setPassword(dto.getPassword());

        return u;
    }
}
