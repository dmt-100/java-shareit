package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingOutDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoShort item;
    private UserDtoShort booker;
    private StatusBooking status;

}