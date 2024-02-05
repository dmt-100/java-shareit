package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.utils.Constants.PATTERN_FOR_BOOKING;

import java.time.LocalDateTime;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class BookingInDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime end;

    private Long itemId;

    private UserDto booker;

    private StatusBooking status;

}