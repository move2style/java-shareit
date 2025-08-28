package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    static final Map<Long, User> userMap = new HashMap<>();

    @Override
    public Collection<User> findAllUser() {
        return userMap.values();
    }

    @Override
    public User findUser(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public User creatUser(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto user) {
        if (userMap.containsKey(userId)) {
            User newUser = userMap.get(userId);

            newUser.setName(user.getName());
            newUser.setEmail(user.getEmail());
            return newUser;
        }
        throw new NullPointerException("Нет такого пользовател");
    }

    @Override
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
