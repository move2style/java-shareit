package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private Long owner;
    private String name;
    private String description;
    private Boolean available;
    private List<Comment> commentList;
    private Long requestId;
}
