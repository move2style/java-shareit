package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingCreateDto createBooking(@RequestBody NewBookingRequest booking,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingCreateDto approvedBooking(@PathVariable Long bookingId,
                                            @RequestParam boolean approved,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approvedBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingCreateDto findBookingId(@PathVariable Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingCreateDto> findBookingId(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingsUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingCreateDto> findBookingItemsId(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingsItemUserId(state, userId);
    }
}
