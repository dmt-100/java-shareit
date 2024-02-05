package ru.practicum.shareit.booking.model;

public enum StateBooking {

    ALL, // все бронирования
    CURRENT, // текущие бронирования
    PAST, // завершённые бронирования
    FUTURE, // будущие бронирования
    WAITING, // ожидающие подтверждения бронирования
    REJECTED // отклонённые бронирования

}