package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto get(Long id);

    List<BookingDto> getAll();

    BookingDto create(Long userId, BookingDto request);

    BookingDto patch(Long id, Long userId, BookingDto request);

    void delete(Long id, Long userId);
}
