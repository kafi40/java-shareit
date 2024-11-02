package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

public record ItemRequestResponse(
        Long id,
        String description,
        UserResponse requestor,
        LocalDateTime created
) {
}
