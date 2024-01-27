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
class BookingInDtoTest {
    @Autowired
    private JacksonTester<BookingInDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);

        BookingInDto bookingInDto = new BookingInDto();
        bookingInDto.setId(1L);
        bookingInDto.setStart(startTime);
        bookingInDto.setEnd(endTime);
        bookingInDto.setItemId(1L);
        bookingInDto.setBookerId(1L);
        bookingInDto.setStatus(Status.APPROVED);
        //when
        JsonContent<BookingInDto> result = json.write(bookingInDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(Status.APPROVED.toString());
    }
}