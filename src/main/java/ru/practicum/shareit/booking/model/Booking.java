package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @JoinColumn(name = "item_id")
    @ManyToOne
    private Item item;
    @JoinColumn(name = "booker_id")
    @ManyToOne
    private User booker;

    public BookingState getBookingState() {
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = null;
        switch (status) {
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
