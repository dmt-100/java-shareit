package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Transactional
    @Override
    public Booking createBooking(Booking booking, long userId, long itemId) {
        return bookingRepository.save(validateBooking(booking, userId, itemId));
    }

    @Transactional
    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approved) {
        isUserExist(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("no such booking"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("there is must be owner");
        }
        if ((booking.getStatus() == Status.APPROVED && approved)
                || (booking.getStatus() == Status.REJECTED && !approved)) {
            throw new BadRequestException("Item status is already set");
        }
        if (approved) booking.setStatus(Status.APPROVED);
        else booking.setStatus(Status.REJECTED);

        return booking;
    }

    @Override
    public Booking getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("no such booking"));
        isUserExist(userId);
        if (userId == booking.getBooker().getId()
                || userId == booking.getItem().getOwner().getId()) {
            return booking;
        }
        throw new NotFoundException("user must be owner or booker");
    }

    @Override
    public List<Booking> getAllBookings(long userId, String state, int from, int size) {
        isUserExist(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (getState(state)) {
            case ALL:
                return bookingRepository
                        .findAllByBookerIdOrderByStartDesc(userId, page);
            case PAST:
                return bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
            case CURRENT:
                return bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page);
            case FUTURE:
                return bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
            case WAITING:
                return bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
            case REJECTED:
                return bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> getAllBookingsByOwnerItems(long userId, String state, int from, int size) {
        isUserExist(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (getState(state)) {
            case ALL:
                return bookingRepository
                        .findAllByOwnerIdOrderByStartDesc(userId, page);
            case PAST:
                return bookingRepository
                        .findAllByOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), page);
            case CURRENT:
                return bookingRepository
                        .findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page);
            case FUTURE:
                return bookingRepository
                        .findAllByOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), page);
            case WAITING:
                return bookingRepository
                        .findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, page);
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking validateBooking(Booking booking, long userId, long itemId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("no such user"));
        booking.setBooker(booker);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("no such item"));
        booking.setItem(item);

        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new BadRequestException("wrong booking time");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("item is unavailable");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("booker can't be owner");
        }
        return booking;
    }

    private void isUserExist(long userId) {
        if (userRepository.existsById(userId)) {
            return;
        }
        throw new NotFoundException("no such user");
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
