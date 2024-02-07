package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

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
    private BookingServiceImpl bookingService;

    private final Long userId = 0L;
    private BookingInDto bookingInDto;
    private BookingOutDto bookingOutDto;
    private BookingOutDto bookingOutDto2;

    @BeforeEach
    public void addBookings() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");
        User user = UserMapper.INSTANCE.toUser(userDto);
        user.setId(userDto.getId());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        Item item = ItemMapper.INSTANCE.toItem(itemDto, user);
        item.setId(itemDto.getId());
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("item 2");
        itemDto2.setDescription("description 2");
        itemDto2.setAvailable(true);
        Item item2 = ItemMapper.INSTANCE.toItem(itemDto2, user);
        item2.setId(itemDto2.getId());

        bookingInDto = new BookingInDto();
        bookingInDto.setId(1L);
        bookingInDto.setStart(LocalDateTime.now().plusMinutes(5));
        bookingInDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingInDto.setItemId(itemDto.getId());
        bookingInDto.setBooker(userDto);
        bookingInDto.setStatus(StatusBooking.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingInDto, user, item);
        booking.setId(bookingInDto.getId());
        bookingOutDto = BookingMapper.INSTANCE.toBookingOutDto(booking);

        BookingInDto bookingInDto2 = new BookingInDto();
        bookingInDto2.setId(2L);
        bookingInDto2.setStart(LocalDateTime.now().plusMinutes(5));
        bookingInDto2.setEnd(LocalDateTime.now().plusHours(3));
        bookingInDto2.setItemId(itemDto2.getId());
        bookingInDto2.setBooker(userDto);
        bookingInDto2.setStatus(StatusBooking.WAITING);
        Booking booking2 = BookingMapper.INSTANCE.toBooking(bookingInDto2, user, item2);
        booking2.setId(bookingInDto2.getId());
        bookingOutDto2 = BookingMapper.INSTANCE.toBookingOutDto(booking2);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все бронирования текущего пользователя, когда вызваны, " +
            "то ответ статус ок и список бронирований")
    void getAllBookingsByUser_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        List<BookingOutDto> bookings = List.of(bookingOutDto, bookingOutDto2);
        when(bookingService.getAllBookingsByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookings), equalTo(result));
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 5);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все бронирования всех вещей владельца, когда вызваны, " +
            "то ответ статус ок и список бронирований")
    void getAllBookingsAllItemsByOwner_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        List<BookingOutDto> bookings = List.of(bookingOutDto, bookingOutDto2);
        when(bookingService.getAllBookingsAllItemsByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookings), equalTo(result));
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 5);
    }

    @SneakyThrows
    @Test
    @DisplayName("получено бронирование по ид, когда бронирование найдено, " +
            "то ответ статус ок, и оно возвращается")
    void getBookingById_whenBookingFound_thenReturnedBooking() {
        long bookingId = 0L;
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingOutDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingOutDto), equalTo(result));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранено бронирование, когда бронирование валидно, " +
            "то ответ статус ок, и оно сохраняется")
    void saveBooking_whenBookingValid_thenSavedBooking() {
        when(bookingService.saveBooking(anyLong(), any(BookingInDto.class))).thenReturn(bookingOutDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingOutDto), equalTo(result));
        verify(bookingService, times(1))
                .saveBooking(anyLong(), any(BookingInDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("обновлено бронирование, когда бронирование валидно, " +
            "то ответ статус ок, и оно обновляется")
    void updateBooking_whenBookingValid_thenUpdatedBooking() {
        long bookingId = 0L;
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutDto2);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingOutDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingOutDto2), equalTo(result));
        verify(bookingService, times(1))
                .updateBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    @DisplayName("обновлено бронирование, когда эппрувд не передан, " +
            "то ответ статус ок, и оно обновляется")
    void updateBooking_whenApprovedNotValid_thenUpdatedBooking() {
        long bookingId = 0L;

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingOutDto2)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"error\":\"Произошла непредвиденная ошибка.\"}", equalTo(result));
        verify(bookingService, never()).updateBooking(userId, bookingId, true);
    }

}