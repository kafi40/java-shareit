package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.base.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class User extends Entity {
    private Long id;
    private String name;
    private String email;
}
