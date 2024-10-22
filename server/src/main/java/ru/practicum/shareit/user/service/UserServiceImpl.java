package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MailAlreadyUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUser(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
    }

    @Override
    public UserResponse createUser(UserDto request) {
        checkSuchEmail(0L, request.getEmail());
        User user = userRepository.save(userMapper.toUser(request));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse patchUser(long userId, UserDto request) {
        User user = null;
        if (request.getEmail() != null) {
            user = checkSuchEmail(userId, request.getEmail());
        }
        if (user == null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        }
        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private User checkSuchEmail(long userId, String email) {
       return userRepository.findByEmailEqualsIgnoreCase(email)
                .map(user -> {
                    if (user.getId().equals(userId)) {
                        return user;
                    } else {
                        throw new MailAlreadyUserException("Почта уже используется");
                    }
                }).orElse(null);
    }
}
