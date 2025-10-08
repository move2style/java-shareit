package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceIntegrationTest extends AbstractIntegrationTest {

    private User owner;

    @BeforeEach
    void setUp() {
        owner = createTestUser("Владелец", "owner@example.com");
    }

    @Test
    void searchItems_WithDifferentCases_Success() {
        ItemDto item1 = createTestItemDto("Дрель электрическая", "Мощная дрель", true);
        ItemDto item2 = createTestItemDto("Отвертка", "дрель отвертка универсальная", true);
        ItemDto item3 = createTestItemDto("Молоток", "Просто молоток", true);

        itemService.addItem(item1, owner.getId());
        itemService.addItem(item2, owner.getId());
        Item unavailableItem = itemService.addItem(item3, owner.getId());

        List<Item> searchResult1 = itemService.findItemWithSearchRequest("дрел");
        assertEquals(2, searchResult1.size());

        List<Item> searchResult2 = itemService.findItemWithSearchRequest("универсал");
        assertEquals(1, searchResult2.size());

        List<Item> searchResult3 = itemService.findItemWithSearchRequest("");
        assertTrue(searchResult3.isEmpty());

        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);
        itemService.updateItem(unavailableItem.getId(), updateDto, owner.getId());

        List<Item> searchResult4 = itemService.findItemWithSearchRequest("молоток");
        assertTrue(searchResult4.isEmpty());
    }

    @Test
    void getItem_WithBookingsAndComments_Success() {
        ItemDto itemDto = createTestItemDto("Тестовый предмет", "Описание", true);
        Item createdItem = itemService.addItem(itemDto, owner.getId());

        User booker = createTestUser("Арендатор", "booker@test.com");

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest pastBooking = createTestBookingDto(
                createdItem.getId(),
                now.minusDays(2),
                now.minusDays(1)
        );
        BookingCreateDto createdPastBooking = bookingService.createBooking(pastBooking, booker.getId());
        bookingService.approvedBooking(createdPastBooking.getId(), true, owner.getId());

        NewBookingRequest futureBooking = createTestBookingDto(
                createdItem.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.createBooking(futureBooking, booker.getId());

        Comment comment = new Comment();
        comment.setText("Отличный предмет!");
        itemService.addComment(createdItem.getId(), booker.getId(), comment);

        ItemUserDto itemWithBookings = itemService.findItemFull(createdItem.getId());

        assertEquals(1, itemWithBookings.getComments().size());
        assertEquals("Отличный предмет!", itemWithBookings.getComments().get(0).getText());
    }

    @Test
    void createAndGetItem_Success() {
        ItemDto itemDto = createTestItemDto("Новый предмет", "Описание предмета", true);
        Item createdItem = itemService.addItem(itemDto, owner.getId());

        assertNotNull(createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getAvailable(), createdItem.getAvailable());

        ItemUserDto retrievedItem = itemService.findItemFull(createdItem.getId());
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
    }

    @Test
    void updateItem_Success() {
        ItemDto itemDto = createTestItemDto("Старый предмет", "Старое описание", true);
        Item createdItem = itemService.addItem(itemDto, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Обновленный предмет");
        updateDto.setDescription("Новое описание");
        updateDto.setAvailable(false);

        Item updatedItem = itemService.updateItem(createdItem.getId(), updateDto, owner.getId());

        assertEquals("Обновленный предмет", updatedItem.getName());
        assertEquals("Новое описание", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void searchItems_Success() {
        ItemDto item1 = createTestItemDto("Поисковый предмет", "Описание для поиска", true);
        ItemDto item2 = createTestItemDto("Другая вещь", "Тоже для поиска", true);

        itemService.addItem(item1, owner.getId());
        itemService.addItem(item2, owner.getId());

        List<Item> searchResults = itemService.findItemWithSearchRequest("поиск");

        assertEquals(2, searchResults.size());
    }

    @Test
    void createComment_Success() {
        User booker = createTestUser("Комментатор", "commentator@example.com");
        ItemDto itemDto = createTestItemDto("Предмет для комментария", "Описание", true);
        Item item = itemService.addItem(itemDto, owner.getId());

        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
        BookingCreateDto booking = bookingService.createBooking(bookingDto, booker.getId());
        bookingService.approvedBooking(booking.getId(), true, owner.getId());

        Comment commentDto = new Comment();
        commentDto.setText("Тестовый комментарий");

        CommentDto createdComment = itemService.addComment(item.getId(), booker.getId(), commentDto);

        assertNotNull(createdComment.getId());
        assertEquals(commentDto.getText(), createdComment.getText());
        assertEquals(booker.getName(), createdComment.getAuthorName());
    }

    @Test
    void updateItem_WhenUserIsNotOwner_ThrowsException() {
        User anotherUser = createTestUser("Другой пользователь", "another@example.com");
        ItemDto itemDto = createTestItemDto("Предмет владельца", "Описание", true);
        Item createdItem = itemService.addItem(itemDto, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Попытка обновления");

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(createdItem.getId(), updateDto, anotherUser.getId()));
    }

    @Test
    void crateItem_WhenUserNotFound_ThrowsException() {
        final long nonExistentUserId = 999L;
        ItemDto itemDto = createTestItemDto("Предмет-призрак", "Описание", true);
        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, nonExistentUserId));
    }

    @Test
    void createComment_WhenUserHasNotBookedItem_ThrowsValidationException() {
        User randomUser = createTestUser("Случайный пользователь", "random@example.com");
        ItemDto itemDto = createTestItemDto("Предмет без аренды", "Описание", true);
        Item item = itemService.addItem(itemDto, owner.getId());

        Comment commentDto = new Comment();
        commentDto.setText("Пытаюсь оставить комментарий");

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(item.getId(), randomUser.getId(), commentDto));
    }

    @Test
    void createComment_WhenBookingIsInFuture_ThrowsValidationException() {
        User booker = createTestUser("Будущий арендатор", "futurebooker@example.com");
        ItemDto itemDto = createTestItemDto("Предмет для будущей аренды", "Описание", true);
        Item item = itemService.addItem(itemDto, owner.getId());

        NewBookingRequest bookingDto = createTestBookingDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        BookingCreateDto booking = bookingService.createBooking(bookingDto, booker.getId());
        bookingService.approvedBooking(booking.getId(), true, owner.getId());

        Comment commentDto = new Comment();
        commentDto.setText("Комментарий из будущего");

        assertThrows(BadRequestException.class,
                () -> itemService.addComment(item.getId(), booker.getId(), commentDto));
    }

    @Test
    void getAllItems_WithBookingsAndComments_Success() {
        ItemDto item1 = createTestItemDto("Первый предмет", "Описание один", true);
        ItemDto item2 = createTestItemDto("Второй предмет", "Описание два", true);

        Item createdItem1 = itemService.addItem(item1, owner.getId());
        Item createdItem2 = itemService.addItem(item2, owner.getId());

        User booker = createTestUser("Арендатор", "booker@test.com");

        LocalDateTime now = LocalDateTime.now();
        NewBookingRequest pastBooking = createTestBookingDto(
                createdItem1.getId(),
                now.minusDays(2),
                now.minusDays(1)
        );
        BookingCreateDto createdPastBooking = bookingService.createBooking(pastBooking, booker.getId());
        bookingService.approvedBooking(createdPastBooking.getId(), true, owner.getId());

        NewBookingRequest futureBooking = createTestBookingDto(
                createdItem1.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.createBooking(futureBooking, booker.getId());

        Comment comment = new Comment();
        comment.setText("Отличная вещь!");
        itemService.addComment(createdItem1.getId(), booker.getId(), comment);

        List<Item> ownerItems = itemService.findUserItemOwner(owner.getId());

        assertEquals(2, ownerItems.size());

        Item firstItem = ownerItems.stream()
                .filter(item -> item.getName().equals("Первый предмет"))
                .findFirst()
                .orElseThrow();

        assertNotNull(firstItem.getAvailable());
        assertEquals("Первый предмет", firstItem.getName());
        assertNull(firstItem.getRequest());
    }
}