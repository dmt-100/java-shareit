package ru.practicum.shareit.booking.exception;

public class BookingNotSaveException extends RuntimeException {

    public BookingNotSaveException(String message) {
        super(message);
    }

}