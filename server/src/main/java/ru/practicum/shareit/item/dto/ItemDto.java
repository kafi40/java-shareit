package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;

@Data
public class ItemDto {
    @MinimalId
    private Long id;
    @NotBlank(message = "Необходимо указать имя")
    private String name;
    @NotBlank(message = "Необходимо указать  описание")
    private String description;
    @NotNull(message = "null недопустимое значение для available")
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}
