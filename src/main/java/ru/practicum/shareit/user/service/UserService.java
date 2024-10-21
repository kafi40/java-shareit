package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse get(Long id);

    UserResponse create(UserDTO request);

    UserResponse patch(Long id, UserDTO request);

    void delete(Long id);

}
