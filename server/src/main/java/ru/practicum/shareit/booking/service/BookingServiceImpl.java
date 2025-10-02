package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.*;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingCreateDto createBooking(NewBookingRequest request, Long userId) {
        User booker = userService.getUserByIdOrThrow(userId);
        Item item = itemService.findItem(request.getItemId());
        LocalDateTime now = LocalDateTime.now();

        if (!item.getAvailable()) {
            throw new UnsupportedOperationException();
        }

        if (request.getStart().isAfter(request.getEnd())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        if (request.getStart().isBefore(now)) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }

        if (request.getEnd().isBefore(now)) {
            throw new IllegalArgumentException("End date cannot be in the past.");
        }

        Booking booking = bookingMapper.toBooking(request, item.getId(), userId, WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        booking = bookingRepository.save(booking);

        BookingCreateDto bookingCreate = bookingMapper.fromNewRequest(request, item, booker, WAITING);
        bookingCreate.setId(booking.getId());
        return bookingCreate;
    }

    @Override
    public Booking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + bookingId));
    }

    @Override
    @Transactional
    public BookingCreateDto approvedBooking(Long bookingId, boolean approved, Long userId) {
        Booking booking = findBooking(bookingId);

        if (!booking.getItem().getOwner().equals(userId)) {
            throw new ForbiddenException("Only the owner can approve or reject the booking");
        } else if (userId.equals(booking.getItem().getOwner())) {
            booking.setStatus(approved ? APPROVED : REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingCreateDto(booking, booking.getItem(), booking.getBooker());
    }

    @Override
    public BookingCreateDto findBookingId(Long bookingId, Long userId) {
        Booking booking = findBooking(bookingId);
        User user = userService.getUserByIdOrThrow(userId);

        if (!booking.getItem().getOwner().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ValidationException("Ошибка");
        }
        return bookingMapper.toBookingCreateDto(booking, booking.getItem(), user);
    }

    @Override
    public List<BookingCreateDto> findBookingsUserId(BookingState state, Long userId) {
        return stateValidate(state, userId);
    }

    @Override
    public List<BookingCreateDto> findBookingsItemUserId(BookingState state, Long userId) {
        if (itemService.findUserItemOwner(userId).size() > 0) {
            return stateValidate(state, userId);
        }
        throw new NotFoundException("Нет вещей у пользователя");
    }

    private List<BookingCreateDto> stateValidate(BookingState state, Long userId) {
        LocalDateTime today = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                List<Booking> bookingCurrent = bookingRepository.findByBookerAndStartLessThanEqualAndEndGreaterThanEqual(userService.getUserByIdOrThrow(userId),
                        today, today);

                return forStateValidate(bookingCurrent, userId);
            case PAST:
                List<Booking> bookingPast = bookingRepository.findByBookerAndEndBefore(userService.getUserByIdOrThrow(userId),
                        today);

                return forStateValidate(bookingPast, userId);
            case FUTURE:
                List<Booking> bookingFuture = bookingRepository.findByBookerAndStartAfter(userService.getUserByIdOrThrow(userId),
                        today);

                return forStateValidate(bookingFuture, userId);
            case WAITING:
                List<Booking> bookingWaiting = bookingRepository.findByBookerAndStatus(userService.getUserByIdOrThrow(userId),
                        WAITING);

                return forStateValidate(bookingWaiting, userId);
            case REJECTED:
                List<Booking> bookingRejected = bookingRepository.findByBookerAndStatus(userService.getUserByIdOrThrow(userId),
                        REJECTED);

                return forStateValidate(bookingRejected, userId);
            default:
                List<Booking> bookingDefault = bookingRepository.findByBooker(userService.getUserByIdOrThrow(userId));

                return forStateValidate(bookingDefault, userId);
        }
    }

    private List<BookingCreateDto> forStateValidate(List<Booking> bookingList, Long userId) {
        List<BookingCreateDto> bookingCreateDtos = new ArrayList<>();

        for (Booking booking : bookingList) {
            User user = userService.getUserByIdOrThrow(userId);
            bookingCreateDtos.add(bookingMapper.toBookingCreateDto(booking, booking.getItem(), user));
        }

        return bookingCreateDtos;
    }
}