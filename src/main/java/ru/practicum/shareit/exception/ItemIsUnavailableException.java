package ru.practicum.shareit.exception;

public class ItemIsUnavailableException extends RuntimeException {
    public ItemIsUnavailableException(String message) {
        super(message);
    }
}
