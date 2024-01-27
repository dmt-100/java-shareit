package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private BookingOutDto bookingOutDto;

    private BookingInDto bookingInDto;

    @BeforeEach
    void setUp() {
        User booker = createUser();
        booker.setId(2L);
        Item item = createItem();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(6));
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);

        bookingOutDto = BookingMapper.mapToBookingOutDto(booking);
        bookingInDto = BookingMapper.mapToBookingInDto(booking);
    }

    @SneakyThrows
    @Test
    void saveBooking_whenBookingIsNotValid_thenMethodArgumentNotValidExceptionThrown() {
        //given
        bookingInDto.setStart(null);
        //when
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).saveBooking(any());

    }

    @SneakyThrows
    @Test
    void saveBooking() {
        when(bookingService.saveBooking(any()))
                .thenReturn(bookingOutDto);
        //when
        String savedBooking = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(bookingOutDto), equalTo(savedBooking));
    }

    @SneakyThrows
    @Test
    void setStatus_whenIllegalStatus_thenIllegalArgumentExceptionThrown() {
        //when
        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, "да!!")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).setStatus(anyLong(), anyLong(), anyBoolean());
    }


    @SneakyThrows
    @Test
    void setStatus() {
        when(bookingService.setStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutDto);
        //when
        String savedBooking = mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, "true")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(bookingOutDto), equalTo(savedBooking));
    }

    @SneakyThrows
    @Test
    void getBooking() {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingOutDto);
        //when
        String booking = mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(bookingOutDto), equalTo(booking));
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState() {
        when(bookingService.findAllBookingsByState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));
        //when
        String bookings = mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(List.of(bookingOutDto)), equalTo(bookings));
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState_whenUnknownParameter_thenReturnBadRequestCode() {
        when(bookingService.findAllBookingsByState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));
        //when
        mvc.perform(get("/bookings?state={state}", "PASTT")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllOwnerBookingsByState() {
        when(bookingService.findAllOwnerBookingsByState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));
        //when
        String bookings = mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(List.of(bookingOutDto)), equalTo(bookings));
    }

    @SneakyThrows
    @Test
    void findAllBookingsOfItem() {
        when(bookingService.findAllBookingsOfItem(anyLong()))
                .thenReturn(List.of(bookingOutDto));
        //when
        String bookings = mvc.perform(get("/bookings/item/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(List.of(bookingOutDto)), equalTo(bookings));
    }

    private Item createItem() {
        User user = createUser();

        Item item = new Item();
        item.setRequestId(null);
        item.setId(1L);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setDescription("мощная");
        item.setOwner(user);
        return item;
    }

    private User createUser() {
        User user = new User();
        user.setEmail("akhraa1@yandex.ru");
        user.setId(1L);
        user.setName("Akhra");
        return user;
    }

}