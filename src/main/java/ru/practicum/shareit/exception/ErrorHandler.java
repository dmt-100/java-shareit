package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSaveException;
import ru.practicum.shareit.user.exception.UserNotUpdateException;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class,
        BookingController.class})
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            BookingNotFoundException.class, CommentNotSaveException.class,
            BookingOtherBookerException.class, ItemOtherOwnerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler({UserNotSaveException.class, UserNotUpdateException.class,
            ItemNotSaveException.class, ItemNotUpdateException.class,
            BookingNotSaveException.class, BookingNotUpdateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotSaveAndUpdate(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final List<ErrorResponse> errorResponses = e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    log.warn(error.getDefaultMessage());
                    return new ErrorResponse(error.getDefaultMessage());
                })
                .collect(Collectors.toList());
        return new ValidationErrorResponse(errorResponses);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }

}