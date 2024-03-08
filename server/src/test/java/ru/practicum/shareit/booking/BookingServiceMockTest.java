package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingControllerTest.createBooking;

@ExtendWith(MockitoExtension.class)
public class BookingServiceMockTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void createBookingWithValidData() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Booking result = bookingService.createBooking(booking, item.getId(), booker.getId());
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getId(), equalTo(result.getId()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getBooker(), equalTo(booker));
    }

    @Test
    void createBookingWithoutUser() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("no such user"));
    }

    @Test
    void createBookingWithoutItem() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("no such item"));
    }

    @Test
    void createBookingWithWrongDateTime() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        booking.setStart(booking.getEnd().plusMinutes(1));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("wrong booking time"));
    }

    @Test
    void createBookingWithUnavailableItem() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.setAvailable(false);
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("item is unavailable"));
    }

    @Test
    void createBookingWithTheSameUserAndBookerId() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(booking, item.getId(), booker.getId()));
        assertThat(exception.getMessage(), equalTo("booker can't be owner"));
    }

    @Test
    void approveBookingWithValidData() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        bookingService.approveBooking(booker.getId(), booking.getId(), true);
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void approveBookingWithValidDataAndStatusRejected() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        bookingService.approveBooking(booker.getId(), booking.getId(), false);
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void approveBookingWithoutBookingEntity() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("no such booking"));
    }

    @Test
    void approveBookingWithoutItemOwner() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("there is must be owner"));
    }

    @Test
    void approveBookingWhenStatusApproved() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        booking.setStatus(Status.APPROVED);
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
        assertThat(exception.getMessage(), equalTo("Item status is already set"));
    }

    @Test
    void approveBookingWhenStatusRejected() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        item.getOwner().setId(booker.getId());
        booking.setStatus(Status.REJECTED);
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), false));
        assertThat(exception.getMessage(), equalTo("Item status is already set"));
    }

    @Test
    void getBookingWithValidData() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        Booking result = bookingService.getBooking(booker.getId(), booking.getId());
        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getItem(), equalTo(booking.getItem()));
        assertThat(result.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getBookingWithoutBookingEntity() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), booking.getId()));
        assertThat(exception.getMessage(), equalTo("no such booking"));
    }

    @Test
    void getBookingWithoutBooker() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(booker.getId()))
                .thenReturn(false);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booker.getId(), booking.getId()));
        assertThat(exception.getMessage(), equalTo("no such user"));
    }

    @Test
    void getBookingWithDifferentUserAndBookerId() {
        Booking booking = createBooking();
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(3L))
                .thenReturn(true);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(3L, booking.getId()));
        assertThat(exception.getMessage(), equalTo("user must be owner or booker"));
    }

    @Test
    void getAllBookingsWithValidData() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        PageRequest page = PageRequest.of(0, 3);
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, page))
                .thenReturn(bookings);
        List<Booking> all = bookingService.getAllBookings(booker.getId(), "ALL", 0, 3);
        List<Booking> past = bookingService.getAllBookings(booker.getId(), "PAST", 0, 3);
        List<Booking> current = bookingService.getAllBookings(booker.getId(), "CURRENT", 0, 3);
        List<Booking> future = bookingService.getAllBookings(booker.getId(), "FUTURE", 0, 3);
        List<Booking> waiting = bookingService.getAllBookings(booker.getId(), "WAITING", 0, 3);
        List<Booking> rejected = bookingService.getAllBookings(booker.getId(), "REJECTED", 0, 3);

        assertThat(all.get(0).getId(), equalTo(booking.getId()));
        assertThat(past.get(0).getId(), equalTo(booking.getId()));
        assertThat(current.get(0).getId(), equalTo(booking.getId()));
        assertThat(future.get(0).getId(), equalTo(booking.getId()));
        assertThat(waiting.get(0).getId(), equalTo(booking.getId()));
        assertThat(rejected.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void getAllBookingsWithWrongState() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookings(booker.getId(), "Unknown", 0, 3));
        assertThat(exception.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }

    @Test
    void getAllBookingsByOwnerItemsWithValidData() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        PageRequest page = PageRequest.of(0, 3);
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        when(bookingRepository.findAllByOwnerIdOrderByStartDesc(booker.getId(), page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING, page))
                .thenReturn(bookings);
        when(bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(booker.getId(), Status.REJECTED, page))
                .thenReturn(bookings);
        List<Booking> all = bookingService.getAllBookingsByOwnerItems(booker.getId(), "ALL", 0, 3);
        List<Booking> past = bookingService.getAllBookingsByOwnerItems(booker.getId(), "PAST", 0, 3);
        List<Booking> current = bookingService.getAllBookingsByOwnerItems(booker.getId(), "CURRENT", 0, 3);
        List<Booking> future = bookingService.getAllBookingsByOwnerItems(booker.getId(), "FUTURE", 0, 3);
        List<Booking> waiting = bookingService.getAllBookingsByOwnerItems(booker.getId(), "WAITING", 0, 3);
        List<Booking> rejected = bookingService.getAllBookingsByOwnerItems(booker.getId(), "REJECTED", 0, 3);

        assertThat(all.get(0).getId(), equalTo(booking.getId()));
        assertThat(past.get(0).getId(), equalTo(booking.getId()));
        assertThat(current.get(0).getId(), equalTo(booking.getId()));
        assertThat(future.get(0).getId(), equalTo(booking.getId()));
        assertThat(waiting.get(0).getId(), equalTo(booking.getId()));
        assertThat(rejected.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void getAllBookingsByOwnerItemsWithWrongState() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        when(userRepository.existsById(booker.getId()))
                .thenReturn(true);
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingsByOwnerItems(booker.getId(), "Unknown", 0, 3));
        assertThat(exception.getMessage(), equalTo("Unknown state: UNSUPPORTED_STATUS"));
    }
}
