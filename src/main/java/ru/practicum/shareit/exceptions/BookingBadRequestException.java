package ru.practicum.shareit.exceptions;

public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(String message) {
        super(message);
    }
}
