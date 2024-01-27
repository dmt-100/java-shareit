package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutDtoTest {
    @Autowired
    private JacksonTester<BookingOutDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);

        BookingOutDto bookingOutDto = new BookingOutDto();
        bookingOutDto.setId(1L);
        bookingOutDto.setStart(startTime);
        bookingOutDto.setEnd(endTime);
        bookingOutDto.setItem(null);
        bookingOutDto.setBooker(null);
        bookingOutDto.setStatus(Status.APPROVED);
        //when
        JsonContent<BookingOutDto> result = json.write(bookingOutDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.booker").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }
}