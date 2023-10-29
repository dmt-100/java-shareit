package ru.practicum.shareit.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, int errorCode) {
        super(message + " Код ошибки: " + errorCode);
    }

}