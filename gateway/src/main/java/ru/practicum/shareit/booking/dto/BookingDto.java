package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
	private Long id;
	@NotNull(message = "null недопустимое значение для start")
	@FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
	private LocalDateTime start;
	@NotNull(message = "null недопустимое значение для end")
	@Future(message = "Время окончания бронирования не может быть в прошлом")
	private LocalDateTime end;
	@NotNull(message = "null недопустимое значение для itemId")
	@Positive(message = "Отрицательное значение недопустимо для itemId")
	private Long itemId;
}
