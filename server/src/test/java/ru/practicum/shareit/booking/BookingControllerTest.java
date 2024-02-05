package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    @DisplayName("получены все бронирования пользователя, когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllBookingsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<BookingOutDto>> response = bookingController
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 0);
    }

    @Test
    @DisplayName("получены все бронирования пользователя, когда вызваны, то ответ статус ок и непустое тело")
    void getAllBookingsByUser_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        Long userId = 0L;
        List<BookingOutDto> expectedBookings = Arrays.asList(new BookingOutDto());
        when(bookingService.getAllBookingsByUser(userId, StateBooking.ALL, 0, 0)).thenReturn(expectedBookings);

        ResponseEntity<List<BookingOutDto>> response = bookingController
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedBookings, equalTo(response.getBody()));
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 0);
    }

    @Test
    @DisplayName("получены все бронирования для всех вещей владельца, " +
            "когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllBookingsAllItemsByOwner_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<BookingOutDto>> response = bookingController
                .getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 0);
    }

    @Test
    @DisplayName("получены все бронирования для всех вещей владельца, " +
            "когда вызваны, то ответ статус ок и непустое тело")
    void getAllBookingsAllItemsByOwner_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        Long userId = 0L;
        List<BookingOutDto> expectedBookings = Arrays.asList(new BookingOutDto());
        when(bookingService.getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 0))
                .thenReturn(expectedBookings);

        ResponseEntity<List<BookingOutDto>> response = bookingController
                .getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedBookings, equalTo(response.getBody()));
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, StateBooking.ALL, 0, 0);
    }

    @Test
    @DisplayName("получено бронирование по ид, когда вещь найдена, то ответ статус ок, и оно возвращается")
    void getBookingById_whenBookingFound_thenReturnedBooking() {
        long bookingId = 0L;
        long userId = 0L;
        BookingOutDto expectedBooking = new BookingOutDto();
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(expectedBooking);

        ResponseEntity<BookingOutDto> response = bookingController.getBookingById(bookingId, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedBooking, equalTo(response.getBody()));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    @DisplayName("сохранено бронирование, когда бронирование валидно, " +
            "то ответ статус ок, и оно сохраняется")
    void saveBooking_whenBookingValid_thenSavedBooking() {
        BookingInDto bookingIn = new BookingInDto();
        BookingOutDto expectedBooking = new BookingOutDto();
        long userId = 0L;
        when(bookingService.saveBooking(bookingIn, userId)).thenReturn(expectedBooking);

        ResponseEntity<BookingOutDto> response = bookingController
                .saveBooking(bookingIn, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedBooking, equalTo(response.getBody()));
        verify(bookingService, times(1)).saveBooking(bookingIn, userId);
    }

    @Test
    @DisplayName("обновлено бронирование, когда бронирование валидно, то ответ статус ок, и оно обновляется")
    void updateBooking_whenBookingValid_thenUpdatedBooking() {
        Long bookingId = 0L;
        Long userId = 0L;
        BookingOutDto newBooking = new BookingOutDto();
        newBooking.setStatus(StatusBooking.APPROVED);
        when(bookingService.updateBooking(bookingId, null, userId)).thenReturn(newBooking);

        ResponseEntity<BookingOutDto> response = bookingController
                .updateBooking(bookingId, null, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(newBooking, equalTo(response.getBody()));
        verify(bookingService, times(1))
                .updateBooking(bookingId, null, userId);
    }

}