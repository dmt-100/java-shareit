package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exceptions.BookingBadRequestException;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
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
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void saveBooking_whenItemNotFound_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        BookingInDto notCreatedItemBooking = BookingMapper.mapToBookingInDto(booking);
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.saveBooking(notCreatedItemBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenOwnerIsBooker_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        booking.setBooker(createUser());
        BookingInDto ownerIsBookerBooking = BookingMapper.mapToBookingInDto(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.saveBooking(ownerIsBookerBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenItemNotAvailable_thenBookingBadRequestExceptionThrown() {
        //given
        Booking booking = createBooking();
        booking.getItem().setAvailable(false);
        BookingInDto unavailableItemBooking = BookingMapper.mapToBookingInDto(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        //when
        Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.saveBooking(unavailableItemBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenBookingTimeIsWrong_thenBookingBadRequestExceptionThrown() {
        //given
        Booking booking = createBooking();
        booking.setStart(LocalDateTime.now().plusDays(100));
        BookingInDto wrongTimeBooking = BookingMapper.mapToBookingInDto(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        //when
        Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.saveBooking(wrongTimeBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenItemAlreadyBooked_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        BookingInDto unavailableDtesBooking = BookingMapper.mapToBookingInDto(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        when(bookingRepository.findTimeCrossingBookings(anyLong(), any(), any())).thenReturn(List.of(booking));
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.saveBooking(unavailableDtesBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenUserNotFound_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        BookingInDto uncreatedUserBooking = BookingMapper.mapToBookingInDto(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.saveBooking(uncreatedUserBooking));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenUserAndItemFoundAndNoTimeCrossing_thenReturnSavedBooking() {
        //given
        Booking booking = createBooking();
        BookingInDto bookingInDto = BookingMapper.mapToBookingInDto(booking);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(booking.getItem()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        when(bookingRepository.save(any())).thenReturn(booking);
        //when
        bookingService.saveBooking(bookingInDto);
        //then
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertThat(savedBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void setStatus_whenBookingNotFound_thenContentNotFountExceptionThrown() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        Long uncreatedBookingId = 1L;
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.setStatus(uncreatedBookingId, 1L, true));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void setStatus_whenUserIsNotOwnerOfItem_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        Long bookingId = 1L;
        Long userNotOwnerOfItemId = 2L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.setStatus(bookingId, userNotOwnerOfItemId, true));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void setStatus_whenStatusAlreadySetInGivenValue_thenBookingBadRequestExceptionThrown() {
        //given
        Booking booking = createBooking();
        Boolean alreadySetStatus = true;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        //when
        Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> bookingService.setStatus(1L, 1L, alreadySetStatus));
        //then
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void setStatus_whenBookingFoundAndUserIsOwner_thenReturnUpdatedUser() {
        //given
        Booking booking = createBooking();
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.setStatus(1L, 1L, true);
        //when
        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        //then
        verify(bookingRepository, times(1)).save(any());
        assertThat(savedBooking.getId(), equalTo(1L));
        assertThat(savedBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getBooking_whenBookingNotFound_thenContentNotFountExceptionThrown() {
        //given
        Long uncreatedBookingId = 1L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.getBooking(1L, uncreatedBookingId));
    }

    @Test
    void getBooking_whenUserIsNotOwnerOrBooker_thenContentNotFountExceptionThrown() {
        //given
        Booking booking = createBooking();
        Long notOwnerUserId = 3L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.getBooking(notOwnerUserId, 1L));
    }

    @Test
    void getBooking_whenAllIsOk_thenReturnBooking() {
        //given
        Booking booking = createBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        //when
        BookingOutDto bookingOutDto = bookingService.getBooking(1L, 1L);
        //then
        assertThat(bookingOutDto.getId(), equalTo(booking.getId()));
    }

    @Test
    void findAllBookingsByState_whenUserNotFound_thenContentNotFountExceptionThrown() {
        //given
        Long uncreatedUserId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.findAllBookingsByState(uncreatedUserId, State.ALL, 0, 10));
    }

    @Test
    void findAllBookingsByState_whenAllBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllBookingsByState(1L, State.ALL, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByBookerId(anyLong(), any());
    }

    @Test
    void findAllBookingsByState_whenCurrentBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllBookingsByState(1L, State.CURRENT, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByCurrentBooker(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByState_whenPastBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllBookingsByState(1L, State.PAST, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsBefore(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByState_whenFutureBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllBookingsByState(1L, State.FUTURE, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByBookerIdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByState_whenBookingsWithStatusRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllBookingsByState(1L, State.REJECTED, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllOwnerBookingsByState_whenUserNotFound_thenContentNotFountExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> bookingService.findAllOwnerBookingsByState(1L, State.ALL, 0, 10));
    }

    @Test
    void findAllOwnerBookingsByState_whenAllBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllOwnerBookingsByState(1L, State.ALL, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByItemOwnerId(anyLong(), any());
    }

    @Test
    void findAllOwnerBookingsByState_whenCurrentBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllOwnerBookingsByState(1L, State.CURRENT, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByOwnerCurrentBooker(anyLong(), any(), any());
    }

    @Test
    void findAllOwnerBookingsByState_whenPastBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllOwnerBookingsByState(1L, State.PAST, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByItemOwnerIdAndEndIsBefore(anyLong(), any(), any());
    }

    @Test
    void findAllOwnerBookingsByState_whenFutureBookingsRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllOwnerBookingsByState(1L, State.FUTURE, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void findAllOwnerBookingsByState_whenBookingsWithStatusRequired_thenInvokeRepositoryMethod() {
        //given
        Booking booking = createBooking();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booking.getBooker()));
        //when
        bookingService.findAllOwnerBookingsByState(1L, State.REJECTED, 0, 10);
        //then
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsOfItem() {
        //given
        Booking booking = createBooking();
        when(bookingRepository.findByItemId(anyLong(), any())).thenReturn(List.of(booking));
        //when
        List<BookingOutDto> bookingOutDtoList = bookingService.findAllBookingsOfItem(1L);
        //then
        assertThat(booking.getId(), equalTo(bookingOutDtoList.get(0).getId()));
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

    private Booking createBooking() {
        User booker = createUser();
        booker.setId(2L);
        Item item = createItem();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().minusDays(4));
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        return booking;
    }
}