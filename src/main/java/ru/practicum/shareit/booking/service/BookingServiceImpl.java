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

        BookingCreateDto bookingCreat = bookingMapper.fromNewRequest(request, item, booker, WAITING);
        bookingCreat.setId(booking.getId());
        return bookingCreat;
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
            if (approved) {
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
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
                List<BookingCreateDto> bookingCreateDtos = new ArrayList<>();
                List<Booking> booking = bookingRepository.findByBookerAndStartLessThanEqualAndEndGreaterThanEqual(userService.getUserByIdOrThrow(userId),
                        today, today);

                for (Booking booking1 : booking) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos.add(bookingMapper.toBookingCreateDto(booking1,
                            booking1.getItem(),
                            user));
                }

                return bookingCreateDtos;
            case PAST:
                List<BookingCreateDto> bookingCreateDtos1 = new ArrayList<>();
                List<Booking> booking1 = bookingRepository.findByBookerAndEndBefore(userService.getUserByIdOrThrow(userId),
                        today);

                for (Booking booking2 : booking1) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos1.add(bookingMapper.toBookingCreateDto(booking2, booking2.getItem(),
                            user));
                }

                return bookingCreateDtos1;
            case FUTURE:
                List<BookingCreateDto> bookingCreateDtos2 = new ArrayList<>();
                List<Booking> booking2 = bookingRepository.findByBookerAndStartAfter(userService.getUserByIdOrThrow(userId),
                        today);

                for (Booking booking3 : booking2) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos2.add(bookingMapper.toBookingCreateDto(booking3,
                            booking3.getItem(),
                            user));
                }

                return bookingCreateDtos2;
            case WAITING:
                List<BookingCreateDto> bookingCreateDtos3 = new ArrayList<>();
                List<Booking> booking3 = bookingRepository.findByBookerAndStatus(userService.getUserByIdOrThrow(userId),
                        WAITING);

                for (Booking booking4 : booking3) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos3.add(bookingMapper.toBookingCreateDto(booking4, booking4.getItem(), user));
                }

                return bookingCreateDtos3;
            case REJECTED:
                List<BookingCreateDto> bookingCreateDtos4 = new ArrayList<>();
                List<Booking> booking4 = bookingRepository.findByBookerAndStatus(userService.getUserByIdOrThrow(userId),
                        REJECTED);

                for (Booking booking5 : booking4) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos4.add(bookingMapper.toBookingCreateDto(booking5, booking5.getItem(), user));
                }

                return bookingCreateDtos4;
            default:
                List<BookingCreateDto> bookingCreateDtos5 = new ArrayList<>();
                List<Booking> booking5 = bookingRepository.findByBooker(userService.getUserByIdOrThrow(userId));

                for (Booking booking6 : booking5) {
                    User user = userService.getUserByIdOrThrow(userId);
                    bookingCreateDtos5.add(bookingMapper.toBookingCreateDto(booking6, booking6.getItem(), user));
                }
                return bookingCreateDtos5;
        }
    }
}