package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponse getBookingForUser(long bookingId, long userId);

    BookingResponse createBooking(long userId, BookingDto request);

    BookingResponse patchBooking(long bookingId, long userId, BookingDto request);

    void deleteBooking(long bookingId, long userId);

    BookingResponse acceptBooking(long bookingId, long userId, boolean isAccept);

    List<BookingResponse> getBookings(long bookerId, BookingState state, long from, long size);

    List<BookingResponse> getBookingsForOwner(long ownerId, BookingState state);
}
