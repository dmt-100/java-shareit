package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return BookingMapper.bookingToResponseDto(
                bookingService.createBooking(
                        BookingMapper.dtoToBooking(bookingRequestDto), userId, bookingRequestDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        return BookingMapper.bookingToResponseDto(bookingService.approveBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        return BookingMapper.bookingToResponseDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return BookingMapper.allBookingsToDto(bookingService.getAllBookings(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsByOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                               @RequestParam(defaultValue = "ALL") String state,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        return BookingMapper.allBookingsToDto(bookingService.getAllBookingsByOwnerItems(userId, state, from, size));
    }
}
