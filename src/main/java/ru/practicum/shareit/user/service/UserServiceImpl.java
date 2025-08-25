package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> findAllUser() {
        return userRepository.findAllUser();
    }

    @Override
    public User findUser(Long userId) {
        return userRepository.findUser(userId);
    }

    @Override
    public User creatUser(User user) {
        validateUser(user);
        return userRepository.creatUser(user);
    }

    @Override
    public User updateUser(Long userId, UserDto user) {
        validateUserUpdate(userId, user);
        return userRepository.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.findUser(userId) == null) {
            throw new NullPointerException();
        }
        userRepository.deleteUser(userId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getName() == null) {
            throw new NullPointerException("Не все данные заполнены");
        }

        if (!user.getEmail().contains("@")) {
            throw new UnsupportedOperationException();
        }

        validateUserUpdateEmail(user);
    }

    private void validateUserUpdate(Long userId, UserDto user) {
        User userOld = userRepository.findUser(userId);
        if (user.getEmail() != null && user.getName() != null) {
            userOld.setName(user.getName());
            userOld.setEmail(user.getEmail());
            validateUserUpdateEmail(userOld);
        } else if (user.getEmail() != null) {
            userOld.setEmail(user.getEmail());
            validateUserUpdateEmail(userOld);
        }
    }

    private void validateUserUpdateEmail(User user) {
        List<User> listUser = new LinkedList<>(userRepository.findAllUser());
        for (User users : listUser) {
            if (users.getEmail().equals(user.getEmail()) && users.getId().equals(user.getId())) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
