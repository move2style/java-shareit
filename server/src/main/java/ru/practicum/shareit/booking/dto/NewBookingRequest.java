package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewBookingRequest {
    @NotNull(message = "Id вещи не должен быть пустым")
    private Long itemId;

    @NotNull(message = "Укажите дату и время начала бронирования")
    @Future(message = "Дата начала бронирования должна быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Укажите дату и время окончания бронирования")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}