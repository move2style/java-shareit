package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private ItemUserDto itemUserDto;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(8);

        User user = new User();
        user.setId(1L);
        user.setName("Randomname");
        user.setEmail("Randomemail");

        itemDto = new ItemDto();
        itemDto.setName("RandomName");
        itemDto.setDescription("RandomDescription");
        itemDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setItem(item);
        commentDto.setId(1L);
        commentDto.setText("text");

        item = new Item();
        item.setId(12L);
        item.setOwner(1L);
        item.setName("RandomName");
        item.setDescription("RandomDescription");


        itemUserDto = new ItemUserDto();
        itemUserDto.setId(2L);
        itemUserDto.setOwner(3L);
        itemUserDto.setName("ItemUserDTO random name");
        itemUserDto.setDescription("ItemUserDTO random description");
        itemUserDto.setAvailable(false);
        itemUserDto.setLastBooking(start);
        itemUserDto.setNextBooking(end);
        itemUserDto.setComments(null);
    }

    @Test
    void findItemTest() throws Exception {
        when(itemService.findItemFull(anyLong())).thenReturn(itemUserDto);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void findUserItemTest() throws Exception {
        List<ItemUserDto> items = Collections.singletonList(itemUserDto);

        when(itemService.findUserItem(anyLong())).thenReturn(items);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyLong())).thenReturn(item);

        //проверка с использованием jsonPath
        mockMvc.perform(post("/items")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(item);

        //проверка с использованием jsonPath
        mockMvc.perform(patch("/items/1")
                        .header(userIdHeader, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    void findItemWithSearchRequestTest() throws Exception {
        List<Item> items = Collections.singletonList(item);

        when(itemService.findItemWithSearchRequest(anyString())).thenReturn(items);

        //проверка с использованием jsonPath
        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].description").value(item.getDescription()))
                .andExpect(jsonPath("$[0].name").value(item.getName()));
    }

    @Test
    void createCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(commentDto);

        //проверка с использованием jsonPath
        mockMvc.perform(post("/items/1/comment")
                        .header(userIdHeader, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}
