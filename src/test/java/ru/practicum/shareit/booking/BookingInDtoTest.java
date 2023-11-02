package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.utils.Constants.FORMATTER_FOR_DATETIME;

import java.time.LocalDateTime;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInDtoTest {

    @Autowired
    private JacksonTester<BookingInDto> json;

    @Test
    @DisplayName("получен ДТО бронирования, когда вызвана сериализация, " +
            "то получено сериализованное бронирование")
    void testBookingInDtoDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(5L);
        BookingInDto bookingDto = new BookingInDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setItemId(2L);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(StatusBooking.WAITING);

        JsonContent<BookingInDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(FORMATTER_FOR_DATETIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(FORMATTER_FOR_DATETIME));
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
    }

}