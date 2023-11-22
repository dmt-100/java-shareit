package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final String xSharerUserId = "X-Sharer-User-Id";
    private final BookingService bookingService;

    /**
     * Получение списка всех бронирований пользователя
     */
    @GetMapping
    public ResponseEntity<List<BookingOutDto>> getAllBookingsByUser(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "ALL") StateBooking state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsByUser(userId, state, from, size);
        log.info("Получен список всех бронирований текущего пользователя с id = {}, количество = {}.",
                userId, bookingOutDtos.size());
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    /**
     * Получение списка бронирований для всех вещей владельца
     */
    @GetMapping("/owner")
    public ResponseEntity<List<BookingOutDto>> getAllBookingsAllItemsByOwner(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "ALL") StateBooking state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsAllItemsByOwner(userId, state, from, size);
        log.info("Получен список всех бронирований для всех вещей владельца с id = {}, " +
                "количество = {}.", userId, bookingOutDtos.size());
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    /**
     * Получение данных о конкретном бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingOutDto> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader(xSharerUserId) Long userId) {
        BookingOutDto bookingOutDto = bookingService.getBookingById(bookingId, userId);
        log.info("Получено бронирование с id = {}.", bookingId);
        return ResponseEntity.ok(bookingOutDto);
    }

    /**
     * Добавление нового запроса на бронирование
     */
    @PostMapping
    @Validated
    public ResponseEntity<BookingOutDto> saveBooking(
            @Valid @RequestBody BookingInDto bookingInDto,
            @RequestHeader(xSharerUserId) Long userId) {
        BookingOutDto bookingOutDto = bookingService.saveBooking(bookingInDto, userId);
        log.info("Добавлен новый запрос на бронирование: {}", bookingOutDto);
        return ResponseEntity.ok(bookingOutDto);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingOutDto> updateBooking(
            @PathVariable Long bookingId, @RequestParam Boolean approved,
            @RequestHeader(xSharerUserId) Long userId) {
        BookingOutDto bookingOutDto = bookingService.updateBooking(bookingId, approved, userId);
        log.info("Обновлено бронирование: {}.", bookingOutDto);
        return ResponseEntity.ok(bookingOutDto);
    }

}