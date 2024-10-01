package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {
    public static BookingDto mapTo(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapTo(booking.getItem()))
                .booker(UserMapper.mapTo(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapFrom(BookingDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(ItemMapper.mapFrom(bookingDto.getItem()))
                .booker(UserMapper.mapFrom(bookingDto.getBooker()))
                .status(bookingDto.getStatus())
                .build();
    }

    public static void updateField(Booking booking, BookingDto bookingDto) {
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(ItemMapper.mapFrom(bookingDto.getItem()));
        booking.setBooker(UserMapper.mapFrom(bookingDto.getBooker()));
        booking.setStatus(bookingDto.getStatus());
    }
}
