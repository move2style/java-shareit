package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addRequest(ItemRequestDto request, Long userId);

    List<ItemRequestDto> getOwnRequests(Long userId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    ItemRequestDto getRequestById(Long requestId, Long userId);
}
