package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto mapTo(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapFrom(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static void updateField(User user, UserDto userDto) {
        if (userDto.getName() != null)
            user.setName(userDto.getName());

        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());
    }
}