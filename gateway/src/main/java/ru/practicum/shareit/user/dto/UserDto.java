package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @PositiveOrZero
    private Long id;
    @NotBlank(message = "Необходимо указать имя")
    @Size(min = 2, max = 16, message = "Проверьте длину имени")
    private String name;
    @Email(message = "Некорректная электронная почта")
    @NotNull(message = "Необходимо указать электронную почту")
    private String email;
}
