package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class MappingItem {
    public ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setName(item.getName());
        dto.setRequest(item.getRequest());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }
}
