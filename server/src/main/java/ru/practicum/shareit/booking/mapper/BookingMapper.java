package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    @Mapping(target = "state", source = ".")
    BookingResponse toBookingResponse(Booking booking);

    List<BookingResponse> toBookingResponseList(List<Booking> bookings);

    Booking toBooking(BookingDto bookingDTO);

    default BookingState getState(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        BookingState bookingState = null;
        switch (booking.getStatus()) {
            case APPROVED -> {
                if (now.isAfter(start) && now.isBefore(end)) bookingState = BookingState.CURRENT;
                else if (now.isBefore(start)) bookingState = BookingState.FUTURE;
                else if (now.isAfter(end)) bookingState = BookingState.PAST;
            }
            case WAITING -> {
                if (now.isBefore(start)) bookingState = BookingState.WAITING;
                else if (now.isAfter(start)) bookingState = BookingState.REJECTED;
            }
            case REJECTED -> bookingState = BookingState.REJECTED;
        }
        return bookingState;
    }
}
