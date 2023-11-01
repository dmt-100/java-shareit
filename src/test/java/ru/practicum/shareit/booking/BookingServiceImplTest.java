package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotSaveException;
import ru.practicum.shareit.booking.exception.BookingNotUpdateException;
import ru.practicum.shareit.booking.exception.BookingOtherBookerException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.exception.ItemOtherOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("получены все бронирования, когда вызваны по умолчанию, то получен пустой список")
    void getAllBookingsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<BookingOutDto> actualItems = bookingService
                .getAllBookingsByUser(userId, StateBooking.ALL, 0, 1);

        assertThat(actualItems, empty());
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdOrderByEndDesc(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все бронирования, когда вызваны прошедшие, то получен непустой список")
    void getAllBookingsByUser_whenInvokedPast_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingOutDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, StateBooking.PAST, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все бронирования, когда вызваны будущие, то получен непустой список")
    void getAllBookingsByUser_whenInvokedFuture_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingOutDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, StateBooking.FUTURE, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все бронирования для вещей владельца, " +
            "когда вызваны ожидающий подтверждение, то получен непустой список")
    void getAllBookingsAllItemsByOwner_whenInvokedWaiting_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingOutDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, StateBooking.WAITING, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все бронирования для вещей владельца, " +
            "когда вызваны отклоненные, то получен непустой список")
    void getAllBookingsAllItemsByOwner_whenInvokedRejected_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingOutDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, StateBooking.REJECTED, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    @DisplayName("получено бронирование по ид, когда бронирование найдено, тогда оно возвращается")
    void getBookingById_whenBookingFound_thenReturnedItem() {
        long bookingId = 0L;
        long userId = 0L;
        Booking expectedBooking = new Booking();
        User user = new User();
        user.setId(userId);
        expectedBooking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        BookingOutDto actualBooking = bookingService.getBookingById(bookingId, userId);

        assertThat(BookingMapper.INSTANCE.toBookingOutDto(expectedBooking), equalTo(actualBooking));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    @DisplayName("получено бронирование по ид, когда бронирование не найдено, " +
            "тогда выбрасывается исключение")
    void getBookingById_whenBookingNotFound_thenExceptionThrown() {
        long bookingId = 0L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, 0L));

        assertThat("Бронирование с идентификатором 0 не найдено.", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    @DisplayName("получено бронирование по ид, когда бронирование не валидно, " +
            "тогда выбрасывается исключение")
    void getBookingById_whenBookingNotValid_thenExceptionThrown() {
        long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        long bookingId = 0L;
        Booking expectedBooking = new Booking();
        expectedBooking.setItem(item);
        expectedBooking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        final BookingOtherBookerException exception = assertThrows(BookingOtherBookerException.class,
                () -> bookingService.getBookingById(bookingId, 1L));

        assertThat("Пользователь с id = 1 не осуществлял бронирование с id = 0",
                equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("сохранено бронирование, когда бронирование валидно, тогда оно сохраняется")
    void saveBooking_whenBookingValid_thenSavedBooking() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        Long itemId = 0L;
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user);

        BookingInDto bookingToSave = new BookingInDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(BookingMapper.INSTANCE.toBooking(bookingToSave, user, item));

        BookingOutDto actualBooking = bookingService.saveBooking(bookingToSave, userId);

        assertThat(bookingToSave.getId(), equalTo(actualBooking.getId()));
        assertThat(bookingToSave.getStart(), equalTo(actualBooking.getStart()));
        assertThat(bookingToSave.getEnd(), equalTo(actualBooking.getEnd()));
        assertThat(bookingToSave.getItemId(), equalTo(actualBooking.getItem().getId()));
        assertThat(1L, equalTo(actualBooking.getBooker().getId()));
        assertThat(null, equalTo(actualBooking.getStatus()));

        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("сохранено бронирование, когда бронирование не сохранено, " +
            "тогда выбрасывается исключение")
    void saveBooking_whenBookingNotSaved_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        Long itemId = 0L;
        item.setAvailable(true);
        item.setOwner(user);

        BookingInDto bookingToSave = new BookingInDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new BookingNotSaveException("Бронирование не было создано"));

        final BookingNotSaveException exception = assertThrows(BookingNotSaveException.class,
                () -> bookingService.saveBooking(bookingToSave, userId));

        assertThat("Бронирование не было создано", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("обновлено бронирование, когда бронирование валидно, тогда оно обновляется")
    void updateBooking_whenBookingFound_thenUpdatedBooking() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        Booking newBooking = new Booking();
        newBooking.setStatus(StatusBooking.APPROVED);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(newBooking);

        BookingOutDto actualBooking = bookingService.updateBooking(bookingId, true, userId);

        assertThat(newBooking.getId(), equalTo(actualBooking.getId()));
        assertThat(newBooking.getStart(), equalTo(actualBooking.getStart()));
        assertThat(newBooking.getEnd(), equalTo(actualBooking.getEnd()));
        assertThat(newBooking.getItem(), equalTo(actualBooking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(actualBooking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(actualBooking.getStatus()));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
    }

    @Test
    @DisplayName("обновлено бронирование, когда бронирование не валидно, тогда выбрасывается исключение")
    void updateBooking_whenBookingNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final ItemOtherOwnerException exception = assertThrows(ItemOtherOwnerException.class,
                () -> bookingService.updateBooking(bookingId, true, userId));

        assertThat(String.format("Пользователь с id = 0 не является владельцем вещи: "
                + ItemMapper.INSTANCE.toItemDto(item)), equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    @DisplayName("обновлено бронирование, когда бронирование не может быть обновлено, " +
            "тогда выбрасывается исключение")
    void updateBooking_whenBookingNotUpdate_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));
        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenThrow(new BookingNotUpdateException("Бронирование не было обновлено"));

        final BookingNotUpdateException exception = assertThrows(BookingNotUpdateException.class,
                () -> bookingService.updateBooking(bookingId, true, userId));

        assertThat("Бронирование не было обновлено", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
    }

}