package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static ru.practicum.shareit.user.dto.UserMapper.toDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.findUser(userId);
    }

    @GetMapping
    public Collection<User> getUsersList() {
        return userService.findAllUser();
    }

    @PostMapping
    public User creatUser(@RequestBody User user) {
        return userService.creatUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User user) {
        return userService.updateUser(userId, toDto(user));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
