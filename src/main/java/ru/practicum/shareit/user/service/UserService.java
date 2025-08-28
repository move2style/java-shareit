package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<User> findAllUser();

    User findUser(Long userId);

    User creatUser(User user);

    User updateUser(Long userId, UserDto user);

    void deleteUser(Long userId);
}
