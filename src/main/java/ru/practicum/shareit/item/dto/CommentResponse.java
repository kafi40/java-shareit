package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String text,
    LocalDateTime created,
    ItemResponse item,
    String authorName
) {

}
