package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ShortBooking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingControllerTest {

    ObjectMapper mapper;
    MockMvc mvc;

    @MockBean
    BookingService bookingService;


    static BookingRequestDto createBookingRequest() {
        return BookingRequestDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(11))
                .itemId(1)
                .build();
    }

    static Booking createBooking() {
        User booker = User.builder()
                .id(1)
                .name("booker")
                .email("booker@test.com")
                .build();
        User owner = User.builder()
                .id(2)
                .name("owner")
                .email("owner@test.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("Pen")
                .description("smth")
                .owner(owner)
                .available(true)
                .build();
        return Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(11))
                .booker(booker)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    @Test
    @SneakyThrows
    void createBookingWithValidData() {
        Booking booking = createBooking();
        BookingRequestDto requestDto = createBookingRequest();
        when(bookingService.createBooking(any(), anyLong(), anyLong()))
                .thenReturn(booking);
        String json = mapper.writeValueAsString(requestDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("Pen"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void createBookingWithoutUserId() {
        Booking booking = createBooking();
        BookingRequestDto requestDto = createBookingRequest();
        when(bookingService.createBooking(any(), anyLong(), anyLong()))
                .thenReturn(booking);
        String json = mapper.writeValueAsString(requestDto);
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).createBooking(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getBookingByIdWithValidData() {
        Booking booking = createBooking();
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(booking);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("Pen"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void getBookingByIdWithNullPath() {
        Booking booking = createBooking();
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(booking);
        mvc.perform(get("/bookings/null")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void approveBookingWithValidData() {
        Booking booking = createBooking();
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.item.id").value(1),
                        jsonPath("$.item.name").value("Pen"),
                        jsonPath("$.status").value("WAITING"),
                        jsonPath("$.booker.id").value(1),
                        jsonPath("$.booker.name").value("booker"),
                        jsonPath("$.start").isNotEmpty(),
                        jsonPath("$.end").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void approveBookingWithoutUserId() {
        Booking booking = createBooking();
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getAllBookingsWithValidData() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void getAllBookingsWithoutUserId() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getAllBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwnerItemsWithValidData() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllBookingsByOwnerItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class)
                );
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwnerItemsWithoutUserId() {
        List<Booking> bookings = List.of(createBooking());
        when(bookingService.getAllBookingsByOwnerItems(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);
        mvc.perform(get("/bookings/owner")
                        .param("state", "WAITING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(bookingService, never()).getAllBookingsByOwnerItems(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void bookingToShortBooking() {
        ShortBooking shortBooking;
        Booking booking = createBooking();
        shortBooking = BookingMapper.bookingToShortBooking(booking);
        assertNotNull(shortBooking);
        assertThat(shortBooking.getId(), equalTo(booking.getId()));
    }
}
