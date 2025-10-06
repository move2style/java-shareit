package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Component
@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemDtoToItem(ItemDto userDto);

    ItemUserDto itemUserDtoToItem(Item item);

    @Mappings({
            @Mapping(target = "authorName",
                    expression = "java(comment.getAuthor() != null ? comment.getAuthor().getName() : null)")
    })
    CommentDto toCommentDto(Comment comment);

    @Mappings({
            @Mapping(target = "itemId", source = "id"),
            @Mapping(target = "ownerId", source = "owner")
    })
    ItemAnswerDto toItemAnswerDto(Item item);
}
