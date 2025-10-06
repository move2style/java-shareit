package ru.practicum.shareit.integration;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.dto.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

class BookingServiceIntegrationTest extends AbstractIntegrationTest {

    private User owner;
    private User booker;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        owner = createTestUser("Хозяин", "owner@example.com");
        booker = createTestUser("Бронировщик", "booker@example.com");

        ItemDto itemDto = createTestItemDto("Тестовый айтем", "Тестовый дескришион", true);
        item = itemService.addItem(itemDto, owner.getId());
    }

    @Test
    void createAndGetBooking_Success() {
        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingCreateDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(booker.getId(), createdBooking.getBooker().getId());

        BookingCreateDto retrievedBooking = bookingService.findBookingId(createdBooking.getId(), booker.getId());
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
        assertEquals(createdBooking.getStatus(), retrievedBooking.getStatus());
    }

    @Test
    void confirmBooking_Success() {
        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingCreateDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());
        BookingCreateDto confirmedBooking = bookingService.approvedBooking(createdBooking.getId(), true, owner.getId());

        assertEquals(APPROVED, confirmedBooking.getStatus());
    }

    @Test
    void getAllBooking_Success() {
        NewBookingRequest booking1 = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        NewBookingRequest booking2 = createTestBookingDto(
                item.getId(),
                now.plusDays(3),
                now.plusDays(4)
        );

        bookingService.createBooking(booking1, booker.getId());
        bookingService.createBooking(booking2, booker.getId());

        List<BookingCreateDto> bookings = bookingService.findBookingsUserId(ALL, booker.getId());

        assertEquals(2, bookings.size());
    }

    @Test
    void getAllItemsBookings_Success() {
        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        bookingService.createBooking(bookingDto, booker.getId());

        List<BookingCreateDto> ownerBookings = bookingService.findBookingsItemUserId(ALL, owner.getId());

        assertEquals(0, ownerBookings.size());
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsException() {
        ItemDto itemDto = createTestItemDto("Недоступный предмет", "Описание", false);
        Item unavailableItem = itemService.addItem(itemDto, owner.getId());

        NewBookingRequest bookingDto = createTestBookingDto(
                unavailableItem.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        assertThrows(UnsupportedOperationException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }


    @Test
    void confirmBooking_NotByOwner_ThrowsException() {
        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingCreateDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());

        User randomUser = createTestUser("Случайный", "random@example.com");
        assertThrows(ForbiddenException.class,
                () -> bookingService.approvedBooking(createdBooking.getId(), true, randomUser.getId()));
    }

    @Test
    void getAllBooking_DifferentStates_Success() {
        NewBookingRequest futureBooking = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.createBooking(futureBooking, booker.getId());

        List<BookingCreateDto> futureBookings = bookingService.findBookingsUserId(FUTURE, booker.getId());
        assertEquals(1, futureBookings.size());

        List<BookingCreateDto> waitingBookings = bookingService.findBookingsUserId(WAITING, booker.getId());
        assertEquals(1, waitingBookings.size());

        BookingCreateDto toReject = bookingService.createBooking(createTestBookingDto(
                item.getId(),
                now.plusDays(3),
                now.plusDays(4)
        ), booker.getId());
        bookingService.approvedBooking(toReject.getId(), false, owner.getId());

        List<BookingCreateDto> rejectedBookings = bookingService.findBookingsUserId(REJECTED, booker.getId());
        assertEquals(1, rejectedBookings.size());
    }

    @Test
    void getBooking_ByNotOwnerOrBooker_ThrowsException() {
        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingCreateDto createdBooking = bookingService.createBooking(bookingDto, booker.getId());
        User randomUser = createTestUser("Случайный", "random@example.com");

        assertThrows(ValidationException.class,
                () -> bookingService.findBookingId(createdBooking.getId(), randomUser.getId()));
    }

    @Test
    void getAllBooking_ForPastState_Success() {
        NewBookingRequest pastBookingDto = createTestBookingDto(
                item.getId(),
                now.minusHours(4),
                now.minusHours(2)
        );
        BookingCreateDto createdBooking = bookingService.createBooking(pastBookingDto, booker.getId());

        bookingService.approvedBooking(createdBooking.getId(), true, owner.getId());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<BookingCreateDto> pastBookings = bookingService.findBookingsUserId(PAST, booker.getId());
        assertFalse(pastBookings.isEmpty());
        assertEquals(1, pastBookings.size());
        assertEquals(createdBooking.getId(), pastBookings.get(0).getId());
    }

    @Test
    void getAllBooking_ForCurrentState_Success() {
        NewBookingRequest currentBookingDto = createTestBookingDto(
                item.getId(),
                now.minusDays(1),
                now.plusDays(1)
        );
        BookingCreateDto createdBooking = bookingService.createBooking(currentBookingDto, booker.getId());
        bookingService.approvedBooking(createdBooking.getId(), true, owner.getId());

        List<BookingCreateDto> currentBookings = bookingService.findBookingsUserId(CURRENT, booker.getId());
        assertFalse(currentBookings.isEmpty());
        assertEquals(1, currentBookings.size());
        assertEquals(createdBooking.getId(), currentBookings.get(0).getId());
    }

    @Test
    void getAllBooking_ForNonExistentUser_ThrowsNotFoundException() {
        long nonExistentUserId = 999L;
        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingsItemUserId(ALL, nonExistentUserId));
    }
}