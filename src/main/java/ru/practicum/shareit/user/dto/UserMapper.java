package ru.practicum.shareit.user.dto;


import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User toUserFull(Long id, UserDto userDto) {
        User user = new User();
        user.setId(id);
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }
}
