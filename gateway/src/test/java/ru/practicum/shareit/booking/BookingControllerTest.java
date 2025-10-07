package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class BookingControllerTest {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingClient bookingClient;
    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setId(1L);
        createDto.setStart(start);
        createDto.setEnd(end);

        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        mvc.perform(post("/bookings")
                        .header(userIdHeader, 1)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookings() throws Exception {
        when(bookingClient.findBookingsUserId(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok(Map.of("bookings", "[]")));

        mvc.perform(get("/bookings")
                        .header(userIdHeader, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}