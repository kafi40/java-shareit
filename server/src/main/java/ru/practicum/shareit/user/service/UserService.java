package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse getUser(long userId);

    UserResponse createUser(UserDto request);

    UserResponse patchUser(long userId, UserDto request);

    void deleteUser(long userId);

}
