package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {

    private Long id; // уникальный идентификатор бронирования
    @NonNull
    private LocalDateTime start; // дата и время начала бронирования
    @NonNull
    private LocalDateTime end; // дата и время конца бронирования
    @NonNull
    private final Item item; // вещь, которую пользователь бронирует
    @NonNull
    private final User booker; // пользователь, который осуществляет бронирование
    @NonNull
    private StatusBooking status; //  статус бронирования

}