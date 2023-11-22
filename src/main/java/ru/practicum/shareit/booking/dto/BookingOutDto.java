package ru.practicum.shareit.booking.dto;

import static ru.practicum.shareit.utils.Constants.PATTERN_FOR_BOOKING;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingOutDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime end;

    private ItemDtoShort item;

    private UserDtoShort booker;

    private StatusBooking status;

}