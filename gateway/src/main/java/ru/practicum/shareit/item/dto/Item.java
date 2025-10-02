package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long id;
    private Long owner;
    private  String name;
    private String description;
    private Boolean available;
    private Long request;
}
