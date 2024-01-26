package ru.practicum.shareit.exceptions;

public class EditingNotAllowedException extends RuntimeException {
    public EditingNotAllowedException(String message) {
        super(message);
    }
}
