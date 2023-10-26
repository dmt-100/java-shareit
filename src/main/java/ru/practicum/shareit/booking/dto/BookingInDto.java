package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookingInDto {

    private Long id;

    @NotNull(message = "Дата и время начала бронирования не могут быть null.")
    @FutureOrPresent(message = "Дата и время начала бронирования не могут быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Дата и время конца бронирования не могут быть null.")
    @FutureOrPresent(message = "Дата и время конца бронирования не могут быть в прошлом.")
    private LocalDateTime end;

    @NotNull(message = "У бронирования должна быть вещь.")
    private Long itemId;

    private UserDto booker;

    private StatusBooking status;

}