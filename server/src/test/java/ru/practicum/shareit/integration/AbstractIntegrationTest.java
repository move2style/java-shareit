package ru.practicum.shareit.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public abstract class AbstractIntegrationTest {

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected BookingServiceImpl bookingService;

    @Autowired
    protected RequestService itemRequestService;

    protected User createTestUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.creatUser(user);
    }

    protected ItemDto createTestItemDto(String name, String description, boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    protected NewBookingRequest createTestBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingRequest bookingDto = new NewBookingRequest();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }

    protected ItemRequestDto createTestItemRequestDto(String description) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(description);
        return requestDto;
    }
}