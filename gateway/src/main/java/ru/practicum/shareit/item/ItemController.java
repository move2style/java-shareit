package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable Long itemId) {
        return itemClient.findItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.findUserItem(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                           @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemWithSearchRequest(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                    @RequestBody Comment comment,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createComment(itemId, comment, userId);
    }
}
