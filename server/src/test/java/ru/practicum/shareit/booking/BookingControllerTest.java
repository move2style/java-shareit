package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private NewBookingRequest newBookingRequest;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("Randomname");
        user.setEmail("Randomemail");

        Item item = new Item();
        item.setId(12L);
        item.setOwner(1L);
        item.setName("RandomName");
        item.setDescription("RandomDescription");

        Long id = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(8);
        newBookingRequest = NewBookingRequest
                .builder().start(start).end(end).build();

        bookingCreateDto = BookingCreateDto
                .builder().id(id).start(start).end(end).booker(user).item(item).build();
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(NewBookingRequest.class), anyLong()))
                .thenReturn(bookingCreateDto);

        //проверка с использованием jsonPath
        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void approvedBookingTest() throws Exception {
        bookingCreateDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approvedBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingCreateDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(userIdHeader, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(bookingCreateDto.getStatus().toString()));
    }

    @Test
    void findBookingIdTest() throws Exception {
        when(bookingService.findBookingId(anyLong(), anyLong()))
                .thenReturn(bookingCreateDto);

        mockMvc.perform(get("/bookings/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findBookingsIdTest() throws Exception {
        List<BookingCreateDto> bookings = Collections.singletonList(bookingCreateDto);

        when(bookingService.findBookingsUserId(any(), anyLong()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void findBookingItemsIdTest() throws Exception {
        List<BookingCreateDto> bookings = Collections.singletonList(bookingCreateDto);

        when(bookingService.findBookingsItemUserId(any(), anyLong()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}