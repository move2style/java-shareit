package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemRequestDto addRequest(ItemRequestDto request, Long userId) {
        ItemRequest itemRequest = mapper.requestDtoToItemRequest(request);
        itemRequest.setRequestor(userRepository.getReferenceById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return mapper.requestToItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        return requestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::requestToItemRequestDto)
                .toList();

    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId) {
        return requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::requestToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        ItemRequestDto itemRequestDto = mapper.requestToItemRequestDto(requestRepository.getReferenceById(requestId));

        List<Item> answers = itemRepository.findByRequest(requestId);
        System.out.println(answers.size());

        itemRequestDto.setItems(answers.stream().map(itemMapper::toItemAnswerDto).toList());

        return itemRequestDto;
    }
}
