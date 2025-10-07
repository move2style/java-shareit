package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItGateway.class)
class ItemControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private MockMvc mvc;

    @Test
    void getItem() throws Exception {
        when(itemClient.findItem(anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(get("/items/{itemId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems() throws Exception {
        when(itemClient.searchItems(anyString()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items/search")
                        .param("text", "search query")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Обновляемый предмет");
        itemDto.setDescription("Реально ведь обновляемый предмет");
        itemDto.setAvailable(true);
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(userIdHeader, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        ItemDto createDto = new ItemDto();
        createDto.setName("New Item");
        createDto.setDescription("New Description");
        createDto.setAvailable(true);
        createDto.setCommentList(null);
        when(itemClient.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/items")
                        .header(userIdHeader, 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}