package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    static final Map<Long, Item> itemMap = new HashMap<>();


    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        Item item = new Item();
        item.setId(getNextId());
        item.setOwner(userId);
        item.setDescription(itemDto.getDescription());
        item.setName(itemDto.getName());
        item.setRequest(itemDto.getRequest());
        item.setAvailable(itemDto.getAvailable());
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item itemOld = findItem(itemId);

        itemOld.setName(itemDto.getName());
        itemOld.setDescription(itemDto.getDescription());
        itemOld.setAvailable(itemDto.getAvailable());
        return itemOld;
    }

    @Override
    public Item findItem(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> findUserItem(Long userId) {
        List<Item> itemList = new ArrayList<>();

        for (Item item : itemMap.values()) {
            if (item.getOwner() == userId) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public List<Item> findItemWithSearchRequest(String text) {
        List<Item> itemList = new ArrayList<>();

        if (text.isEmpty() || text.isBlank()) {
            return itemList;
        }

        for (Item item : itemMap.values()) {
            if (item.getAvailable() != null && item.getAvailable()) {
                if (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) {
                    itemList.add(item);
                } else if (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    itemList.add(item);
                }
            }
        }

        return itemList;
    }


    public long getNextId() {
        long currentMaxId = itemMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
