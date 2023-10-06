package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {

    private Long id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private final Item item;
    @NonNull
    private final User booker;
    @NonNull
    private StatusBooking status;

}