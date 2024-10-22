package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    @Positive
    private Long id;
    @Size(min = 1, max = 512, message = "Проверьте длину описания")
    private String description;
    @Positive
    private Long requestor;
    private LocalDateTime created;
}
