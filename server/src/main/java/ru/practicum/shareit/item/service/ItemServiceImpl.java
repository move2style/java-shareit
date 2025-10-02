package ru.practicum.shareit.item.service;


import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUserDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item addItem(ItemDto itemDto, Long userId) {
        validateCreatItem(itemDto, userId);
        Item item = mapper.itemDtoToItem(itemDto);
        item.setOwner(userId);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemDto.getRequestId());
        }
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Не указан айди пользователя");
        }

        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            //проверить подходит ли хоязин
            if (!userId.equals(item.getOwner())) {
                throw new NotFoundException("Пользователь не подходит");
            }

            // Обновляем поля item из itemDto (только если они указаны)
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }

            return itemRepository.save(item);
        } else {
            throw new NotFoundException("Нет такого предмета");
        }
    }

    @Override
    public ItemUserDto findItemFull(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));
        List<Comment> commentList = commentRepository.findByItem_Id(itemId);
        ItemUserDto itemUserDto = mapper.itemUserDtoToItem(item);
        itemUserDto.setComments(commentList);
        itemUserDto.setNextBooking(null);
        itemUserDto.setLastBooking(null);

        return itemUserDto;
    }

    @Override
    public Item findItem(Long itemId) {

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));
    }

    @Override
    public Object findUserItem(Long userId) {
        List<Item> itemsOwner = itemRepository.findItemByOwnerId(userId);
        List<ItemUserDto> itemUserDtos = new ArrayList<>();

        for (Item item : itemsOwner) {
            List<LocalDateTime> bookingDates = new ArrayList<>();
            List<Booking> bookings = bookingRepository.findByItem(item);
            ItemUserDto itemUserDto = mapper.itemUserDtoToItem(item);

            if (bookings.size() != 0) {
                bookingDates.add(bookings.getFirst().getEnd());
                bookingDates.add(bookings.getLast().getEnd());

                itemUserDto.setLastBooking(bookingDates.get(1)); // Заполняем даты бронирования в DTO ТЕКУЩЕЙ вещи
                itemUserDto.setNextBooking(bookingDates.get(0)); // Заполняем даты бронирования в DTO ТЕКУЩЕЙ вещи

                itemUserDtos.add(itemUserDto);
            } else {
                itemUserDto.setLastBooking(null);
                itemUserDto.setNextBooking(null);
            }
            List<Comment> commentList = commentRepository.findByItem_Id(item.getId());
            itemUserDto.setComments(commentList);
            itemUserDtos.add(itemUserDto);
        }

        return itemUserDtos;
    }

    @Override
    public List<Item> findUserItemOwner(Long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> findItemWithSearchRequest(String text) {
        if (text.isEmpty() || text.isBlank() || text == null) {
            List<Item> itemList = new ArrayList<>();
            return itemList;
        }
        return itemRepository.searchByNameOrDescription(text);
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, Comment comment) {
        User user = userService.getUserByIdOrThrow(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));
        LocalDateTime now = LocalDateTime.now();

        if (item.getOwner().equals(userId)) {
            throw new ValidationException("You cannot comment on your own item");
        }

        boolean hasPastBooking = bookingRepository
                .existsByItemIdAndBookerIdAndEndBeforeAndStatus(itemId, userId, now, APPROVED);

        if (!hasPastBooking) {
            throw new BadRequestException("You can only comment on items you have previously booked and had booking approved");
        }

        Comment commentAdd = new Comment();
        commentAdd.setText(comment.getText());
        commentAdd.setItem(item);
        commentAdd.setAuthor(user);
        commentAdd.setCreated(now);
        commentRepository.save(commentAdd);

        return mapper.toCommentDto(commentAdd);
    }

    private void validateCreatItem(ItemDto itemDto, Long userId) {
        if (userId != null) {
            Optional<User> user = userService.findUser(userId);
            if (user == null || user.isEmpty()) {
                throw new NotFoundException("нет такого пользователя");
            }
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Не указана доступность");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Не указано название предмета");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank() || itemDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Не указано описание предмета");
        }
    }

}
