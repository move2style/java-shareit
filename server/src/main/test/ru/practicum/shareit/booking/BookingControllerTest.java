package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final String userIdHeader = "X-Sharer-User-Id";

    @Test
    void createBookingTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Randomname");
        user.setEmail("Randomemail");

        Item item = new Item();
        item.setId(123L);
        item.setOwner(1L);
        item.setName("RandomName");
        item.setDescription("RandomDescription");

        Long id = 123L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(8);
        NewBookingRequest newBookingRequest = NewBookingRequest
                .builder().start(start).end(end).build();

        BookingCreateDto bookingCreateDto = BookingCreateDto
                .builder().id(id).start(start).end(end).booker(user).item(item).build();


        when(bookingService.createBooking(any(NewBookingRequest.class), anyLong()))
                .thenReturn(bookingCreateDto);

        //проверка с использованием jsonPath
        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123L));
    }
}