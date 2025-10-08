package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId);

    Item updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemUserDto findItemFull(Long itemId);

    Item findItem(Long itemId);

    Object findUserItem(Long userId);

    List<Item> findUserItemOwner(Long userId);

    List<Item> findItemWithSearchRequest(String text);

    CommentDto addComment(Long itemId, Long userId, Comment comment);
}
