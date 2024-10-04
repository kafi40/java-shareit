package ru.practicum.shareit.user.dto;


import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String name,
        String email
) {
}
