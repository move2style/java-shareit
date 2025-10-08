package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        return userRepository.findById(userId);
    }

    @Override
    public User creatUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDto user) {
        validateUserUpdate(userId, user);
        User userNew = getUserByIdOrThrow(userId);
        if (user.getEmail() != null) {
            userNew.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            userNew.setName(user.getName());
        }
        return userRepository.save(userNew);
    }

    @Override
    public User getUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    @Override
    public void deleteUser(Long userId) {
        if (userRepository.getById(userId) == null) {
            throw new NullPointerException();
        }
        userRepository.deleteById(userId);
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
        User userOld = userRepository.getById(userId);
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
        List<User> listUser = new LinkedList<>(userRepository.findAll());
        for (User users : listUser) {
            if (users.getEmail().equals(user.getEmail()) && !users.getId().equals(user.getId())) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
