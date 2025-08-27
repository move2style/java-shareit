package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    Long id;
    Long owner;
    String name;
    String description;
    Boolean available;
    String request;
}
