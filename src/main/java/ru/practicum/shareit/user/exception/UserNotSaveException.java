package ru.practicum.shareit.user.exception;

public class UserNotSaveException extends RuntimeException {

    public UserNotSaveException(String message) {
        super(message);
    }

}