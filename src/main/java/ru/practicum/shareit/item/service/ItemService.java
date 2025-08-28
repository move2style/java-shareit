package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId);

    Item updateItem(Long itemId, ItemDto itemDto, Long userId);

    Item findItem(Long itemId);

    List<Item> findUserItem(Long userId);

    List<Item> findItemWithSearchRequest(String text);
}
