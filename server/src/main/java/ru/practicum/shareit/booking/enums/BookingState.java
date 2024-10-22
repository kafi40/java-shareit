package ru.practicum.shareit.booking.enums;

public enum BookingState {
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	REJECTED,
	WAITING;

	public static BookingState from(String stringState) {
		return BookingState.valueOf(stringState.toUpperCase());
	}
}
