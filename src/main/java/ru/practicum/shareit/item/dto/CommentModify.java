package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentModify {
    private Long id;
//    @NotBlank(message = "Недопустимо пустое значение для text")
    private String text;
}
