package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOtherOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingOutDto> getAllBookingsByUser(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));

        List<Booking> bookings; // = new ArrayList<>();
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case ("PAST"):
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findByBookerIdAndEndIsAfter(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ("WAITING"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, StatusBooking.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, StatusBooking.REJECTED);
                break;
            default:
                throw new StatusBookingNotFoundException("Unknown state: " + state);
        }

        return BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(bookings);
    }

    @Override
    public List<BookingOutDto> getAllBookingsAllItemsByOwner(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));

        List<Booking> bookings = new ArrayList<>();
        BooleanExpression byOwnerId = QItem.item.owner.id.eq(userId);
        switch (state) {
            case ("ALL"):
                bookingRepository.findAll(byOwnerId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("CURRENT"):
                BooleanExpression byStart = QBooking.booking.start.before(LocalDateTime.now());
                BooleanExpression byEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byStart).and(byEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("PAST"):
                BooleanExpression byBeforeEnd = QBooking.booking.end.before(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byBeforeEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("FUTURE"):
                BooleanExpression byAfterEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byAfterEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("WAITING"):
                BooleanExpression byStatusWaiting = QBooking.booking.status.eq(StatusBooking.WAITING);
                bookingRepository.findAll(byOwnerId.and(byStatusWaiting),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("REJECTED"):
                BooleanExpression byStatusRejected = QBooking.booking.status.eq(StatusBooking.REJECTED);
                bookingRepository.findAll(byOwnerId.and(byStatusRejected),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            default:
                throw new StatusBookingNotFoundException("Unknown state: " + state);
        }
        return BookingMapper.INSTANCE.convertBookingListToBookingOutDTOList(bookings);
    }

    @Override
    public BookingOutDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование с идентификатором " + bookingId + " не найдено."));
        if ((!booking.getBooker().getId().equals(userId)) &&
                (!booking.getItem().getOwner().getId().equals(userId))) {
            throw new BookingOtherBookerException(String.format("Пользователь с id = " + userId +
                    " не осуществлял бронирование с id = : " + bookingId));
        }

        return BookingMapper.INSTANCE.toBookingOutDto(booking);
    }

    @Transactional
    @Override
    public BookingOutDto saveBooking(BookingInDto bookingInDto, Long userId) {
        BookingInDto bookingInDtoNew = validateBookingDto(bookingInDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));
        Item item = itemRepository.findById(bookingInDtoNew.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("Вещь с идентификатором " + bookingInDtoNew.getItemId() + " не найдена."));

        if (!item.getAvailable()) {
            throw new ValidationException("Ошибка! Вещь: " + ItemMapper.INSTANCE.toItemDto(item) +
                    " недоступна для бронирования.", 30003);
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new UserNotFoundException("Пользователь с id = " + userId +
                    " владелец вещи с id = " + item.getId());
        }

        bookingInDtoNew.setStatus(StatusBooking.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingInDtoNew, user, item);
        try {
            return BookingMapper.INSTANCE.toBookingOutDto(bookingRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new BookingNotSaveException("Бронирование не было создано: " + bookingInDto);
        }
    }

    @Transactional
    @Override
    public BookingOutDto updateBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Бронирование с id = " + bookingId + " не найдено."));
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemOtherOwnerException(String.format("Пользователь с id = " + userId +
                    " не является владельцем вещи: " + ItemMapper.INSTANCE.toItemDto(item)));
        }
        if (!booking.getStatus().equals(StatusBooking.WAITING)) {
            throw new ValidationException("Статус бронирования с id = " + bookingId +
                    " не был изменён пользователем с id = " + userId, 30004);
        }

        if (approved) {
            booking.setStatus(StatusBooking.APPROVED);
        } else {
            booking.setStatus(StatusBooking.REJECTED);
        }

        try {
            return BookingMapper.INSTANCE.toBookingOutDto(bookingRepository.saveAndFlush(booking));
        } catch (DataIntegrityViolationException e) {
            throw new BookingNotUpdateException("Бронирование с id = " + bookingId +
                    " не было отклонено или подтверждено.");
        }
    }

    private BookingInDto validateBookingDto(BookingInDto bookingInDto) {
        if (bookingInDto.getEnd().isBefore(bookingInDto.getStart())) {
            throw new ValidationException("Ошибка! Дата и время начала бронирования должны быть раньше " +
                    "даты и времени конца бронирования.", 30001);
        }
        if (bookingInDto.getEnd().isEqual(bookingInDto.getStart())) {
            throw new ValidationException("Ошибка! Дата и время начала бронирования не могут совпадать с " +
                    "датой и временем конца бронирования.", 30002);
        }
        return bookingInDto;
    }

}