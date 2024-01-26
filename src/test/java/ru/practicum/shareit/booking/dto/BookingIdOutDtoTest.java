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
class BookingIdOutDtoTest {
    @Autowired
    private JacksonTester<BookingIdOutDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);

        BookingIdOutDto bookingIdOutDto = new BookingIdOutDto();
        bookingIdOutDto.setId(1L);
        bookingIdOutDto.setStart(startTime);
        bookingIdOutDto.setEnd(endTime);
        bookingIdOutDto.setItemId(1L);
        bookingIdOutDto.setBookerId(1L);
        bookingIdOutDto.setStatus(Status.APPROVED);
        //when
        JsonContent<BookingIdOutDto> result = json.write(bookingIdOutDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }
}