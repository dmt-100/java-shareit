package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {

    List<BookingOutDto> getAllBookingsByUser(Long userId, String state);

    List<BookingOutDto> getAllBookingsAllItemsByOwner(Long userId, String state);

    BookingOutDto getBookingById(Long userId, Long bookingId);

    BookingOutDto saveBooking(BookingInDto bookingDto, Long userId);

    BookingOutDto updateBooking(Long bookingId, Boolean approved, Long userId);

}
