package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDTO {
    @MinimalId
    private Long id;
    private String description;
    private UserDTO requestor;
    private LocalDateTime created;
}
