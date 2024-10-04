package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;

@Data
public class UserModify {
    @MinimalId
    private Long id;
    private String name;
    @Email(message = "Некорректная электронная почта")
    @NotNull(message = "Необходимо указать электронную почту")
    private String email;
}
