package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemUserDto findItem(@PathVariable Long itemId) {
        return itemService.findItemFull(itemId);
    }

    @GetMapping
    public Object findUserItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findUserItem(userId);
    }

    @PostMapping
    public Item addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@PathVariable Long itemId,
                           @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/search")
    public List<Item> findItemWithSearchRequest(@RequestParam String text) {
        return itemService.findItemWithSearchRequest(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId,
                                    @RequestBody Comment comment,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, userId, comment);
    }
}
