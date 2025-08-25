package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    Long id;
    String description;
    Long requestor;
    LocalDate created;
}
