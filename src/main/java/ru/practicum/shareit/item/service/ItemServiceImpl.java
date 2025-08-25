package ru.practicum.shareit.item.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        validateCreatItem(itemDto, userId);
        return itemRepository.addItem(itemDto, userId);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Не указан айди пользователя");
        }

        if (itemRepository.findItem(itemId) != null) {
            Item itemOld = findItem(itemId);
            if (userId != itemOld.getOwner()) {
                throw new NotFoundException("Пользователь не подходит");
            }
            return itemRepository.updateItem(itemId, itemDto, userId);
        }
        throw new NotFoundException("Нет такого предмета");
    }

    @Override
    public Item findItem(Long itemId) {
        return itemRepository.findItem(itemId);
    }

    @Override
    public List<Item> findUserItem(Long userId) {
        return itemRepository.findUserItem(userId);
    }

    @Override
    public List<Item> findItemWithSearchRequest(String text) {
        return itemRepository.findItemWithSearchRequest(text);
    }

    private void validateCreatItem(ItemDto itemDto, Long userId) {
        if (userRepository.findUser(userId) == null) {
            throw new NotFoundException("нет такого пользователя");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Не указана доступность");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Не указано название предмета");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank() || itemDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Не указано описание предмета");
        }
    }
}
