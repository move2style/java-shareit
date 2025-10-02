package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBooking(NewBookingRequest request, Long itemId, Long userId, BookingStatus status);

    @Mapping(target = "id", ignore = true)
    BookingCreateDto fromNewRequest(NewBookingRequest request, Item item, User booker, BookingStatus status);

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    BookingCreateDto toBookingCreateDto(Booking booking, Item item, User booker);
}
