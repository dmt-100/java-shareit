package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exceptions.UnknownStateException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto saveBooking(@Valid @RequestBody BookingInDto bookingDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingDto.setBookerId(userId);
        return bookingService.saveBooking(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto setStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable(name = "bookingId") Long bookingId,
                                   @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.setStatus(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable(name = "bookingId") Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findAllBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String strState,
                                                      @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        State state;
        try {
            state = State.valueOf(strState);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(strState);
        }
        return bookingService.findAllBookingsByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findAllOwnerBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String strState,
                                                           @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Min(1) int size) {
        State state;
        try {
            state = State.valueOf(strState);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(strState);
        }
        return bookingService.findAllOwnerBookingsByState(userId, state, from, size);
    }


    @GetMapping("/item/{itemId}")
    public List<BookingOutDto> findAllBookingsOfItem(@PathVariable(name = "itemId") Long itemId) {
        return bookingService.findAllBookingsOfItem(itemId);
    }
}
