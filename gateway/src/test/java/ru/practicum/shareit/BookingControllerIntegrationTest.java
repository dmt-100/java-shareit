package ru.practicum.shareit;

import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.StatusBooking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;

    private final Long userId = 0L;
    private BookingInDto bookingInDto;

    @BeforeEach
    public void addBooking() {
        bookingInDto = new BookingInDto();
        bookingInDto.setId(1L);
        bookingInDto.setStart(LocalDateTime.now().plusMinutes(5));
        bookingInDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingInDto.setItemId(2L);
        bookingInDto.setStatus(StatusBooking.WAITING);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все бронирования текущего пользователя, когда вызваны с невалидным состоянием, " +
            "то ответ статус бед реквест")
    void getAllBookingsByUser_whenStateNotValid_thenResponseStatusBadRequest() {
        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "not valid")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"error\":\"Unknown state: not valid\"}", equalTo(result));
        verify(bookingClient, never()).getAllBookingsByUser(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранено бронирование, когда бронирование не валидно, " +
            "то ответ статус бед реквест")
    void saveBooking_whenBookingNotValid__thenResponseStatusBadRequest() {
        bookingInDto.setStart(LocalDateTime.now().minusMinutes(10));

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"errorResponses\":[{\"error\":\"Дата и время начала бронирования " +
                "не могут быть в прошлом.\"}]}", equalTo(result));
        verify(bookingClient, never()).saveBooking(anyLong(), any(BookingInDto.class));
    }

}