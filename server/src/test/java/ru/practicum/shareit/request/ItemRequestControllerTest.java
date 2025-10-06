package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;

    private ItemRequestDto request;

    @BeforeEach
    void setUp() {
        request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("DescriptionTest");
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void addItemRequestTest() throws Exception {
        when(requestService.addRequest(any(ItemRequestDto.class), anyLong())).thenReturn(request);

        //проверка с использованием jsonPath
        mockMvc.perform(post("/requests")
                        .header(userIdHeader, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    void getOwnRequestsTest() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(request);

        when(requestService.getOwnRequests(anyLong())).thenReturn(requests);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/requests")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(request.getId()))
                .andExpect(jsonPath("$[0].description").value(request.getDescription()));
    }

    @Test
    void getOtherUsersRequestsTest() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(request);

        when(requestService.getOtherUsersRequests(anyLong())).thenReturn(requests);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(request.getId()))
                .andExpect(jsonPath("$[0].description").value(request.getDescription()));
    }

    @Test
    void getRequestByIdTest() throws Exception {

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(request);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/requests/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }
}
