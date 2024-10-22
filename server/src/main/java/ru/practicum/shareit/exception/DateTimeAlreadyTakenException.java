package ru.practicum.shareit.exception;

public class DateTimeAlreadyTakenException extends RuntimeException {
    public DateTimeAlreadyTakenException(String message) {
        super(message);
    }
}
