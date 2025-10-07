package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestClient itemRequestClient;
    @Autowired
    private MockMvc mvc;

    @Test
    void create() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна мощная дрель");
        when(itemRequestClient.addRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/requests")
                        .header(userIdHeader, 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnRequests() throws Exception {
        when(itemRequestClient.getOwnRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/requests")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getOtherUsersRequests() throws Exception {
        when(itemRequestClient.getOtherUsersRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/requests/all")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById() throws Exception {
        long requestId = 1L;
        when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", requestId)));

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk());
    }
}