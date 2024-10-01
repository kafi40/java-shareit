package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.HeaderExpectedException;

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
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody BookingDto request) {
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public BookingDto patch(@PathVariable Long id,
                            @RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody BookingDto request) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        return bookingService.patch(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        bookingService.delete(id, userId);
    }
}
