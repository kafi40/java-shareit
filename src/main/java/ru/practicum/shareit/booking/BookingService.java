package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponse getForUser(Long id, Long userId);

    BookingResponse create(Long userId, BookingModify request);

    BookingResponse patch(Long id, Long userId, BookingModify request);

    void delete(Long id, Long userId);

    BookingResponse acceptBooking(Long id, Long userId, Boolean isAccept);

    List<BookingResponse> getAllForBooker(Long bookerId, BookingState state);

    List<BookingResponse> getAllForOwner(Long ownerId, BookingState state);
}
