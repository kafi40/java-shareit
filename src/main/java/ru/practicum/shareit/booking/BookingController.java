package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingDto get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.getAll();
    }

    @PostMapping
    public BookingDto create(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto request) {
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public BookingDto patch(@PathVariable Long id,
                            @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody BookingDto request) {
        return bookingService.patch(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingService.delete(id, userId);
    }
}
