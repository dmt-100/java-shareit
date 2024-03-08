package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ShortBooking;
import ru.practicum.shareit.booking.model.enums.Status;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {
    public Booking dtoToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public ShortBooking bookingToShortBooking(Booking booking) {
        return ShortBooking.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public BookingResponseDto bookingToResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .booker(new BookingResponseDto.User(booking.getBooker().getId(), booking.getBooker().getName()))
                .status(booking.getStatus())
                .build();
    }

    public List<BookingResponseDto> allBookingsToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::bookingToResponseDto)
                .collect(Collectors.toList());
    }
}
