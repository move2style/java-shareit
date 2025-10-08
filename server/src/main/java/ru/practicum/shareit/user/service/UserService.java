package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Collection<User> findAllUser();

    Optional<User> findUser(Long userId);

    User creatUser(User user);

    User updateUser(Long userId, UserDto user);

    User getUserByIdOrThrow(Long userId);

    void deleteUser(Long userId);
}
