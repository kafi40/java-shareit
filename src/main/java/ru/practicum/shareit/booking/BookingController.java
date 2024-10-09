package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingResponse getForUser(@PathVariable Long id,
                                      @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getForUser(id, userId);
    }

    @GetMapping
    public List<BookingResponse> getAllForBooker(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                 @RequestParam(
                                                         value = "state",
                                                         required = false,
                                                         defaultValue = "ALL") BookingState state) {
        return bookingService.getAllForBooker(bookerId, state);
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

    @GetMapping("/owner")
    public List<BookingResponse> getAllForOwner(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(
                                                        value = "state",
                                                        required = false,
                                                        defaultValue = "ALL") BookingState state) {
        return bookingService.getAllForOwner(userId, state);
    }
}
