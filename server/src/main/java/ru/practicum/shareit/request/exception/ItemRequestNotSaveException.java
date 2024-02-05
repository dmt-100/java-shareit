package ru.practicum.shareit.request.exception;

public class ItemRequestNotSaveException extends RuntimeException {

    public ItemRequestNotSaveException(String message) {
        super(message);
    }

}