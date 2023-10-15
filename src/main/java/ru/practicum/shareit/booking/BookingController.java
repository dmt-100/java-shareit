package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
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

    private final BookingService bookingService;

    @GetMapping
    /**
     * Получение списка всех бронирований текущего пользователя
     */
    public ResponseEntity<List<BookingOutDto>> getAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsByUser(userId, state);
        log.info("Получен список всех бронирований текущего пользователя с id = {}, количество = {}.",
                userId, bookingOutDtos.size());
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    @GetMapping("/owner")
    /**
     * Получение списка бронирований для всех вещей текущего пользователя
     */
    public ResponseEntity<List<BookingOutDto>> getAllBookingsAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsAllItemsByOwner(userId, state);
        log.info("Получен список всех бронирований для всех вещей текущего пользователя с id = {}, " +
                "количество = {}.", userId, bookingOutDtos.size());
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    @GetMapping("/{bookingId}")
    /**
     * Получение данных о конкретном бронировании
     */
    public ResponseEntity<BookingOutDto> getBookingById(@PathVariable Long bookingId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingOutDto bookingOutDto = bookingService.getBookingById(userId, bookingId);
        log.info("Получено бронирование с id = {}.", bookingId);
        return ResponseEntity.ok(bookingOutDto);
    }

    @PostMapping
    @Validated
    /**
     * Добавление нового запроса на бронирование
     */
    public ResponseEntity<BookingOutDto> saveBooking(@Valid @RequestBody BookingInDto bookingInDto,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingOutDto bookingOutDto = bookingService.saveBooking(bookingInDto, userId);
        log.info("Добавлен новый запрос на бронирование: {}", bookingOutDto);
        return ResponseEntity.ok(bookingOutDto);
    }

    @PatchMapping("/{bookingId}")
    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    public ResponseEntity<BookingOutDto> updateItem(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingOutDto bookingOutDto = bookingService.updateBooking(bookingId, approved, userId);
        log.info("Обновлено бронирование: {}.", bookingOutDto);
        return ResponseEntity.ok(bookingOutDto);
    }

}