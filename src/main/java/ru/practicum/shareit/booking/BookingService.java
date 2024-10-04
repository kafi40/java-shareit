package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse get(Long id);

    List<BookingResponse> getAll();

    BookingResponse create(Long userId, BookingModify request);

    BookingResponse patch(Long id, Long userId, BookingModify request);

    void delete(Long id, Long userId);

    BookingResponse acceptBooking(Long id, Long userId, Boolean isAccept);
}
