package ru.practicum.shareit.item.dto;

public record ItemShortResponse(
        Long id,
        String name,
        Long ownerId
) {
}
