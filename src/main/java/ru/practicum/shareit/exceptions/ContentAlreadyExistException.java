package ru.practicum.shareit.exceptions;

public class ContentAlreadyExistException extends RuntimeException {
    public ContentAlreadyExistException(String message) {
        super(message);
    }
}
