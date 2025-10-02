package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
@Mapper(componentModel = "spring")
public interface RequestMapper {
    ItemRequest requestDtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto requestToItemRequestDto(ItemRequest request);
}
