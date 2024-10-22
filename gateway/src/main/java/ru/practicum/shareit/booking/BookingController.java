package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
													@PathVariable Long bookingId) {
		log.info("Gateway received: Get booking with id={} for user with id={}", bookingId, userId);
		return bookingClient.getBookingForUser(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Gateway received: Get bookings with state={}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto request) {
		log.info("Gateway received: Create booking={} by user with id={}", request, userId);
		return bookingClient.createBooking(userId, request);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> patchBooking(@PathVariable Long bookingId,
										@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
										@RequestBody(required = false) BookingDto request,
										@RequestParam(value = "approved", required = false) Boolean isAccept) {
		if (isAccept != null) {
			log.info("Gateway received: Accept booking with id={} by user with id={}", bookingId, userId);
			return bookingClient.acceptBooking(bookingId, userId, isAccept);
		} else {
			log.info("Gateway received: Patch booking={} by user with id={}", request, userId);
			return bookingClient.patchBooking(bookingId, userId, request);
		}
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<Object> deleteBooking(@PathVariable Long bookingId,
							  @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
		log.info("Gateway received: Delete booking with id={} by user with id={}", bookingId, userId);
		return bookingClient.deleteBooking(bookingId, userId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsForOwner(
			@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam) {
		log.info("Gateway received: Get booking with state={} for user with id={}", stateParam, userId);
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getBookingsForOwner(userId, state);
	}
}
