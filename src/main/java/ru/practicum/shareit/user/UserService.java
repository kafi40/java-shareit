package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserModify;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse get(Long id);

    UserResponse create(UserModify request);

    UserResponse patch(Long id, UserModify request);

    void delete(Long id);

}
