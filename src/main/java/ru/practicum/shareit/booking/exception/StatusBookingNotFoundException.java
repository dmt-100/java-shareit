package ru.practicum.shareit.booking.exception;

public class StatusBookingNotFoundException extends RuntimeException {

    public StatusBookingNotFoundException(String message) {
        super(message);
    }

}