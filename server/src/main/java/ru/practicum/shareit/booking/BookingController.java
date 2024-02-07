package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.StateBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    /**
     * Получение списка всех бронирований текущего пользователя
     */
    public ResponseEntity<List<BookingOutDto>> getAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam StateBooking state,
            @RequestParam Integer from, @RequestParam Integer size) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsByUser(userId, state, from, size);
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    @GetMapping("/owner")
    /**
     * Получение списка бронирований для всех вещей владельца
     */
    public ResponseEntity<List<BookingOutDto>> getAllBookingsAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam StateBooking state,
            @RequestParam Integer from, @RequestParam Integer size) {
        List<BookingOutDto> bookingOutDtos = bookingService.getAllBookingsAllItemsByOwner(userId, state, from, size);
        return ResponseEntity.ok().body(bookingOutDtos);
    }

    @GetMapping("/{bookingId}")
    /**
     * Получение данных о конкретном бронировании
     */
    public ResponseEntity<BookingOutDto> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        BookingOutDto bookingOutDto = bookingService.getBookingById(userId, bookingId);
        return ResponseEntity.ok(bookingOutDto);
    }

    @PostMapping
    /**
     * Добавление нового запроса на бронирование
     */
    public ResponseEntity<BookingOutDto> saveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingInDto bookingInDto) {
        BookingOutDto bookingOutDto = bookingService.saveBooking(userId, bookingInDto);
        return ResponseEntity.ok(bookingOutDto);
    }

    @PatchMapping("/{bookingId}")
    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    public ResponseEntity<BookingOutDto> updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId, @RequestParam Boolean approved) {
        BookingOutDto bookingOutDto = bookingService.updateBooking(userId, bookingId, approved);
        return ResponseEntity.ok(bookingOutDto);
    }

}