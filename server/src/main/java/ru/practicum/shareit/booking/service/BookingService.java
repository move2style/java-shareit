package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingCreateDto createBooking(NewBookingRequest booking, Long userId);

    Booking findBooking(Long bookingId);

    BookingCreateDto approvedBooking(Long bookingId, boolean approved, Long userId);

    BookingCreateDto findBookingId(Long bookingId, Long userId);

    List<BookingCreateDto> findBookingsUserId(BookingState state, Long userId);

    List<BookingCreateDto> findBookingsItemUserId(BookingState state, Long userId);
}
