package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.MinimalId;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @MinimalId
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;

    public boolean isHasPermissionToField(Long userId) {
        if (booker.getId().equals(userId)) {
            return !status.equals(BookingStatus.APPROVED) && !status.equals(BookingStatus.REJECTED);
        } else if (item.getOwner().getId().equals(userId)) {
            if (hasField(start, end, item, booker)) {
                return false;
            }
            return !status.equals(BookingStatus.WAITING) && !status.equals(BookingStatus.CANCELED);
        } else {
            return false;
        }
    }

    private boolean hasField(Object... fields) {
        for (Object field : fields) {
            if (field != null) {
                return false;
            }
        }
        return true;
    }
}
