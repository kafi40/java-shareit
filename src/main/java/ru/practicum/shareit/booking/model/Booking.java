package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.base.Entity;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Crossable;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Booking extends Entity implements Crossable {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @Override
    public boolean isCross(LocalDateTime start, LocalDateTime end) {
        return (!this.start.isBefore(start) && !this.start.isAfter(end)) ||
                (this.end.isBefore(end) && this.end.isAfter(start));
    }
}
