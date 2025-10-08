package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
    private String text;
    private LocalDateTime created;
    private Item item;
    private User author;
}
