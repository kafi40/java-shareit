package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

@Builder
public record ItemRequestResponse(
        Long id,
        String description,
        UserResponse requestor,
        LocalDateTime created
) {
}
