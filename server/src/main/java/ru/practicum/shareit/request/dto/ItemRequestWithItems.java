package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemShortResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ItemRequestWithItems(
        Long id,
        String description,
        UserResponse requestor,
        LocalDateTime created,
        List<ItemShortResponse> items
) {
}
