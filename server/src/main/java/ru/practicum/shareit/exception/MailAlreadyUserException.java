package ru.practicum.shareit.exception;

public class MailAlreadyUserException extends RuntimeException {
    public MailAlreadyUserException(String message) {
        super(message);
    }
}
