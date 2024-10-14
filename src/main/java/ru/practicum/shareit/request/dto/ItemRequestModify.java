package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.user.dto.UserModify;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestModify {
    @MinimalId
    private Long id;
    private String description;
    private UserModify requestor;
    private LocalDateTime created;
}
