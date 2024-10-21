package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MailAlreadyUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse get(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));
    }

    @Override
    public UserResponse create(UserDTO request) {
        checkSuchEmail(0L, request.getEmail());
        User user = userRepository.save(userMapper.toUser(request));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse patch(Long id, UserDTO request) {
        User user = null;
        if (request.getEmail() != null) {
            user = checkSuchEmail(id, request.getEmail());
        }
        if (user == null) {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));
        }
        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User checkSuchEmail(Long userId, String email) {
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
