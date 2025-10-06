package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 9, 22, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 9, 23, 12, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDate.from(start));
        bookingDto.setEnd(LocalDate.from(end));
        bookingDto.setStatus(WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-09-22");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-09-23");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testBookingDtoDeserialization() throws Exception {
        String jsonContent = "{\"id\":1,\"start\":\"2025-09-22\",\"end\":\"2025-09-23\"," +
                "\"status\":\"WAITING\"}";

        BookingDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDate.of(2025, 9, 22));
        assertThat(result.getEnd()).isEqualTo(LocalDate.of(2025, 9, 23));
        assertThat(result.getStatus()).isEqualTo(WAITING);
    }
}