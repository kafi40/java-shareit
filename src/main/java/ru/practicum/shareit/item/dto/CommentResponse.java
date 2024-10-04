package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserResponse;

public record CommentResponse(
    Long id,
    String text,
    ItemResponse item,
    UserResponse author
) {
}
