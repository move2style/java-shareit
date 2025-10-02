package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody NewBookingRequest requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@PathVariable Long bookingId,
                                                  @RequestParam boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.approvedBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingId(@PathVariable long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBookingId(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingId(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.findBookingsUserId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingItemsId(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.findBookingsItemUserId(userId, state);
    }
}
