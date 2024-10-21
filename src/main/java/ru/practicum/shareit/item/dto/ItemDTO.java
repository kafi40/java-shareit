package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.user.dto.UserDTO;

@Data
public class ItemDTO {
    @MinimalId
    private Long id;
    @NotBlank(message = "Необходимо указать имя")
    private String name;
    @NotBlank(message = "Необходимо указать  описание")
    private String description;
    @NotNull(message = "null недопустимое значение для available")
    private Boolean available;
    private UserDTO owner;
    private ItemRequestDTO itemRequestDTO;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer rentCount;
}
