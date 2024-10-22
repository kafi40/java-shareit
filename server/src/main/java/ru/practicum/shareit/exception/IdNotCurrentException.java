package ru.practicum.shareit.exception;

public class IdNotCurrentException extends RuntimeException {
    public IdNotCurrentException(String message) {
        super(message);
    }
}
