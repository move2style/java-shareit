package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestServiceIntegrationTest extends AbstractIntegrationTest {

    private User requester;

    @BeforeEach
    void setUp() {
        requester = createTestUser("запросящий", "requester@example.com");
    }

    @Test
    void getOwnRequests_Success() {
        ItemRequestDto request1 = createTestItemRequestDto("первый запрос");
        ItemRequestDto request2 = createTestItemRequestDto("второй запрос");

        itemRequestService.addRequest(request1, requester.getId());
        itemRequestService.addRequest(request2, requester.getId());

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(requester.getId());

        assertEquals(2, ownRequests.size());
        assertTrue(ownRequests.stream()
                .anyMatch(request -> request.getDescription().equals("первый запрос")));
        assertTrue(ownRequests.stream()
                .anyMatch(request -> request.getDescription().equals("второй запрос")));
    }

    @Test
    void createAndGetRequestById_Success() {
        ItemRequestDto requestDto = createTestItemRequestDto("тестовый запрос");
        ItemRequestDto createdRequest = itemRequestService.addRequest(requestDto, requester.getId());

        assertNotNull(createdRequest.getId());
        assertEquals(requestDto.getDescription(), createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());

        ItemRequestDto retrievedRequest = itemRequestService.getRequestById(createdRequest.getId(), requester.getId());
        assertEquals(createdRequest.getId(), retrievedRequest.getId());
        assertEquals(createdRequest.getDescription(), retrievedRequest.getDescription());
    }

    @Test
    void getOtherUsersRequests_Success() {
        User otherUser = createTestUser("другой пользователь", "other@example.com");
        ItemRequestDto request = createTestItemRequestDto("тестовый запрос");

        itemRequestService.addRequest(request, requester.getId());

        List<ItemRequestDto> otherRequests = itemRequestService.getOtherUsersRequests(otherUser.getId());

        assertFalse(otherRequests.isEmpty());
        assertEquals(request.getDescription(), otherRequests.get(0).getDescription());
    }

    @Test
    void getRequestById_WhenRequestNotFound_ThrowsException() {
        final long nonExistentRequestId = 999L;
        assertThrows(Exception.class,
                () -> itemRequestService.getRequestById(nonExistentRequestId, requester.getId()));
    }

    @Test
    void getRequestById_WithItems_Success() {
        ItemRequestDto requestDto = createTestItemRequestDto("Нужна отвертка");
        ItemRequestDto createdRequest = itemRequestService.addRequest(requestDto, requester.getId());

        User owner = createTestUser("Владелец", "owner.req@example.com");
        ItemDto itemDto = createTestItemDto("Отвертка крестовая", "Отличная отвертка", true);
        itemDto.setRequestId(createdRequest.getId());
        itemService.addItem(itemDto, owner.getId());

        ItemRequestDto retrievedRequest = itemRequestService.getRequestById(createdRequest.getId(), requester.getId());

        assertFalse(retrievedRequest.getItems().isEmpty());
        assertEquals(1, retrievedRequest.getItems().size());
        assertEquals("Отвертка крестовая", retrievedRequest.getItems().get(0).getName());
    }

    @Test
    void getOwnRequests_WhenNoRequests_ReturnsEmptyList() {
        User newUser = createTestUser("Новый пользователь", "new.user@example.com");

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(newUser.getId());

        assertNotNull(ownRequests);
        assertTrue(ownRequests.isEmpty());
    }

    @Test
    void getOtherUsersRequests_WhenNoOtherRequests_ReturnsEmptyList() {
        ItemRequestDto requestDto = createTestItemRequestDto("Единственный запрос");
        itemRequestService.addRequest(requestDto, requester.getId());

        List<ItemRequestDto> otherRequests = itemRequestService.getOtherUsersRequests(requester.getId());

        assertNotNull(otherRequests);
        assertTrue(otherRequests.isEmpty());
    }
}