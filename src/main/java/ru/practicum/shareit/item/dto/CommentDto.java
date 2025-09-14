package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    String text;
    LocalDateTime created;
    String authorName;
    Item item;
    User author;
}
