package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;

@Data
@Builder
public class UserDto {
    @MinimalId
    private Long id;
    private String name;
    @Email
    @NotNull
    private String email;
}
