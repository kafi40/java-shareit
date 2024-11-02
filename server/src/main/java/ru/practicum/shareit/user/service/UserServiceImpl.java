package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.MailAlreadyUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        log.info("Server: Method getUser begin");
        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
    }

    @Override
    public UserResponse createUser(UserDto request) {
        log.info("Server: Method createUser begin");
        checkSuchEmail(0L, request.getEmail());
        User user = userRepository.save(userMapper.toUser(request));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse patchUser(long userId, UserDto request) {
        log.info("Server: Method patchUser begin");
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
        log.info("Server: Method deleteUser begin");
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
