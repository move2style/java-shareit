package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUserDto {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
   private LocalDateTime lastBooking;
   private LocalDateTime nextBooking;
   private List<Comment> comments;
}
