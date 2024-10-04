package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @GetMapping
    public List<BookingResponse> getAll() {
        return bookingService.getAll();
    }

    @PostMapping
    public BookingResponse create(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody BookingModify request) {
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public BookingResponse patch(@PathVariable Long id,
                                 @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody(required = false) BookingModify request,
                                 @RequestParam(value = "approved", required = false) Boolean isAccept) {
        if (isAccept != null && request == null) {
            return bookingService.acceptBooking(id, userId, isAccept);
        } else if (isAccept == null && request != null) {
            return bookingService.patch(id, userId, request);
        } else {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingService.delete(id, userId);
    }
}
