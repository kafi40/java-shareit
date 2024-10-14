package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingModify {
    @MinimalId
    private Long id;
    @NotNull(message = "null недопустимое значение для start")
//    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "null недопустимое значение для end")
    @Future(message = "Время окончания бронирования не может быть в прошлом")
    private LocalDateTime end;
    @NotNull(message = "null недопустимое значение для itemId")
    @Positive(message = "Отрицательное значение недопустимо для itemId")
    private Long itemId;
    private BookingStatus status;
}
