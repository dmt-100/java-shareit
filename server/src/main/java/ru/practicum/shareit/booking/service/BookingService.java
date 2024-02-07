package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.StateBooking;

import java.util.List;

public interface BookingService {

    List<BookingOutDto> getAllBookingsByUser(
            Long userId, StateBooking state, Integer from, Integer size);

    List<BookingOutDto> getAllBookingsAllItemsByOwner(
            Long userId, StateBooking state, Integer from, Integer size);

    BookingOutDto getBookingById(Long userId, Long bookingId);

    BookingOutDto saveBooking(Long userId, BookingInDto bookingDto);

    BookingOutDto updateBooking(Long userId, Long bookingId, Boolean approved);

}