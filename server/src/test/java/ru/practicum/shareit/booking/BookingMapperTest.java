package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ShortBooking;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static ru.practicum.shareit.booking.BookingControllerTest.createBooking;
import static ru.practicum.shareit.booking.BookingControllerTest.createBookingRequest;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingMapperTest {
    Booking booking;
    BookingRequestDto requestDto;
    BookingResponseDto responseDto;
    ShortBooking shortBooking;

    @Test
    void dtoToBooking() {
        requestDto = createBookingRequest();
        booking = BookingMapper.dtoToBooking(requestDto);
        assertNotNull(booking);
        assertThat(booking.getStart(), equalTo(requestDto.getStart()));
    }

    @Test
    void bookingToResponseDto() {
        booking = createBooking();
        responseDto = BookingMapper.bookingToResponseDto(booking);
        assertNotNull(responseDto);
        assertThat(booking.getId(), equalTo(responseDto.getId()));
    }

    @Test
    void bookingToShortBooking() {
        booking = createBooking();
        shortBooking = BookingMapper.bookingToShortBooking(booking);
        assertNotNull(shortBooking);
        assertThat(shortBooking.getId(), equalTo(booking.getId()));
    }

    @Test
    void allBookingsToDto() {
        List<Booking> bookings = List.of(createBooking());
        List<BookingResponseDto> bookingResponseDtoList = BookingMapper.allBookingsToDto(bookings);
        assertThat(bookingResponseDtoList.size(), equalTo(1));
        assertThat(bookingResponseDtoList.get(0).getId(), equalTo(bookings.get(0).getId()));
    }
}
