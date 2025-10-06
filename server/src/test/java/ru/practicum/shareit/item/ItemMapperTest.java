package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;


    @Test
    void toItemAnswerDto() {
        User owner = new User();
        owner.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item.setRequest(null);

        ItemAnswerDto itemDto = itemMapper.toItemAnswerDto(item);

        assertEquals(item.getId(), itemDto.getItemId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(owner.getId(), itemDto.getOwnerId());
    }

    @Test
    void itemDtoToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель");
        itemDto.setAvailable(true);

        Item item = itemMapper.itemDtoToItem(itemDto);

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    void itemUserDtoToItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(10L);
        item.setOwner(owner.getId());

        ItemUserDto itemUserDto = itemMapper.itemUserDtoToItem(item);

        assertEquals(item.getId(), itemUserDto.getId());
        assertEquals(item.getName(), itemUserDto.getName());
        assertEquals(item.getDescription(), itemUserDto.getDescription());
        assertEquals(item.getAvailable(), itemUserDto.getAvailable());
        assertEquals(owner.getId(), itemUserDto.getOwner());
    }

    @Test
    void itemUserDtoToItem_WithNullFields() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        ItemUserDto itemUserDto = itemMapper.itemUserDtoToItem(item);

        assertEquals(1L, itemUserDto.getId());
        assertEquals("Дрель", itemUserDto.getName());
        assertNull(itemUserDto.getDescription());
        assertNull(itemUserDto.getAvailable());
        assertNull(itemUserDto.getOwner());
        assertNull(itemUserDto.getLastBooking());
        assertNull(itemUserDto.getNextBooking());
        assertNull(itemUserDto.getComments());
    }

    @Test
    void itemUserDtoToItem_WithBookingsAndComments() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(10L);
        item.setOwner(owner.getId());

        ItemUserDto itemUserDto = itemMapper.itemUserDtoToItem(item);

        assertEquals(1L, itemUserDto.getId());
        assertEquals("Дрель", itemUserDto.getName());
        assertEquals("Мощная дрель", itemUserDto.getDescription());
        assertEquals(true, itemUserDto.getAvailable());
        assertEquals(10L, itemUserDto.getOwner());

        assertNull(itemUserDto.getLastBooking());
        assertNull(itemUserDto.getNextBooking());
        assertNull(itemUserDto.getComments());
    }

    @Test
    void toCommentDto() {
        User author = new User();
        author.setId(1L);
        author.setName("Иван Иванов");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Отличная дрель, всем рекомендую!");
        comment.setCreated(LocalDateTime.of(2024, 1, 15, 10, 30));
        comment.setAuthor(author);
        comment.setItem(item);

        CommentDto commentDto = itemMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
        assertEquals("Иван Иванов", commentDto.getAuthorName());
        assertEquals(item, commentDto.getItem());
        assertEquals(author, commentDto.getAuthor());
    }

    @Test
    void toCommentDto_WithNullAuthor() {
        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Комментарий без автора");
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);

        CommentDto commentDto = itemMapper.toCommentDto(comment);

        assertEquals(100L, commentDto.getId());
        assertEquals("Комментарий без автора", commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertEquals(item, commentDto.getItem());
        assertNull(commentDto.getAuthor());
    }

    @Test
    void toCommentDto_WithAuthorButNullName() {
        User author = new User();
        author.setId(1L);

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Комментарий от автора без имени");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);

        CommentDto commentDto = itemMapper.toCommentDto(comment);

        assertEquals(100L, commentDto.getId());
        assertEquals("Комментарий от автора без имени", commentDto.getText());
        assertNull(commentDto.getAuthorName()); // authorName должен быть null, так как у автора нет имени
        assertEquals(author, commentDto.getAuthor());
    }

    @Test
    void toCommentDto_WithNullFields() {
        Comment comment = new Comment();
        comment.setId(100L);

        CommentDto commentDto = itemMapper.toCommentDto(comment);

        assertEquals(100L, commentDto.getId());
        assertNull(commentDto.getText());
        assertNull(commentDto.getCreated());
        assertNull(commentDto.getAuthorName());
        assertNull(commentDto.getItem());
        assertNull(commentDto.getAuthor());
    }
}
