package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    Long id;
    LocalDate start;
    Long item;
    LocalDate end;
    String reviews;
    String status;
}
