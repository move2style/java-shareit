package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class InMemoryUserRepository {
    static final Map<Long, User> userMap = new HashMap<>();

    public Collection<User> findAllUser() {
        return userMap.values();
    }

    public User findUser(Long userId) {
        return userMap.get(userId);
    }

    public User creatUser(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long userId, UserDto user) {
        if (userMap.containsKey(userId)) {
            User newUser = userMap.get(userId);

            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            return newUser;
        }
        throw new NullPointerException("Нет такого пользовател");
    }

    public void deleteUser(Long userId) {
        userMap.remove(userId);
    }

    public long getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
