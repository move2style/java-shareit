package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    Item item;
    User booker;

}
