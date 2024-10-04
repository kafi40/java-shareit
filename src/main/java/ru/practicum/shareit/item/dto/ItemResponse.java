package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.dto.UserResponse;

public record ItemResponse(
        Long id,
        String name,
        String description,
        Boolean available,
        UserResponse owner,
        ItemRequestResponse itemRequest,
        Integer rentCount
) {
}
