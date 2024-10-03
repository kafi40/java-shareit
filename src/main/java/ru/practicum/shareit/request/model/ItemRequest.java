package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.base.Entity;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class ItemRequest extends Entity {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
