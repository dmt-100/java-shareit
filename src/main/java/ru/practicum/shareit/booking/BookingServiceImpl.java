package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingOutDto saveBooking(BookingInDto bookingDto) {
        Item item = getItem(bookingDto.getItemId());
        if (item.getOwner().getId().equals(bookingDto.getBookerId())) {
            throw new ContentNotFountException("Вледелец вещи не может ее забронировать");
        }
        if (!item.getAvailable()) {
            throw new BookingBadRequestException("Данная вещь недоступна для бранирования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new BookingBadRequestException("Время начала бронирования не может быть позже либо равным времени его окончания");
        }
        List<Booking> bookingsOfItem = bookingRepository.findTimeCrossingBookings(bookingDto.getItemId(),
                bookingDto.getStart(), bookingDto.getEnd());
        if (!bookingsOfItem.isEmpty()) {
            throw new ContentNotFountException("Данная вещь уже забронирована в запрашиваемые даты");
        }
        User user = getUser(bookingDto.getBookerId());
        bookingDto.setStatus(Status.WAITING);
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingOutDto(savedBooking);
    }

    @Override
    public BookingOutDto setStatus(Long bookingId, Long userId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ContentNotFountException("Бронирования с id = " + bookingId + " не существует"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ContentNotFountException("Бранирование может подтвердить только владелец вещи");
        }
        Status status = isApproved ? Status.APPROVED : Status.REJECTED;
        if (Objects.equals(booking.getStatus(), status)) {
            throw new BookingBadRequestException("Статус в актуальном состоянии");
        }
        booking.setStatus(status);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingOutDto(savedBooking);
    }

    public BookingOutDto getBooking(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (Objects.equals(booking.getItem().getOwner().getId(), userId) || Objects.equals(booking.getBooker().getId(), userId)) {
            return BookingMapper.mapToBookingOutDto(booking);
        }
        throw new ContentNotFountException("Просматривать бронирование может только его автор, " +
                "либо владелец вещи, к которой относится это бронирование");
    }

    @Override
    public List<BookingOutDto> findAllBookingsByState(Long userId, State state, int from, int size) {
        //Просмотр пользователем всех его букингов (то что он забронировал у других пользователей)
        //Проверка существования пользователя
        getUser(userId);
        List<Booking> bookings;
        Pageable pageSortedByStartTime = makePageSortedByStartTime(from, size);
        switch (state) {
            case ALL:
                //тут в тестах постман запрашивается порядковый номер элемента а не страницы потому page = from / size
                bookings = bookingRepository.findByBookerId(userId,
                        makePageSortedByStartTime(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findByCurrentBooker(userId, LocalDateTime.now(),
                        PageRequest.of(from, size, Sort.by("id").ascending()));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        pageSortedByStartTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        pageSortedByStartTime);
                break;
            default:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, Status.valueOf(state.toString()),
                        pageSortedByStartTime);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> findAllOwnerBookingsByState(Long ownerId, State state, int from, int size) {
        //Просмотр владельцем вещей всех букингов этих вещей
        //Проверка существования пользователя
        getUser(ownerId);
        List<Booking> bookings;
        Pageable pageSortedByStartTime = makePageSortedByStartTime(from, size);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(ownerId,
                        pageSortedByStartTime);
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerCurrentBooker(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, Sort.by("id").ascending()));
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(),
                        pageSortedByStartTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(),
                        pageSortedByStartTime);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Status.valueOf(state.toString()),
                        pageSortedByStartTime);
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> findAllBookingsOfItem(Long itemId) {
        return bookingRepository.findByItemId(itemId, Sort.by("start").descending()).stream()
                .map(BookingMapper::mapToBookingOutDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ContentNotFountException("Пользователя с id = " + userId + " не существует"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ContentNotFountException("Вещи с id = " + itemId + " не существует"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ContentNotFountException("Бранирования с id = " + bookingId + " не существует"));
    }

    private PageRequest makePageSortedByStartTime(int from, int size) {
        return PageRequest.of(from, size, Sort.by("start").descending());
    }
}
