package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.MailAlreadyUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto get(Long id) {
        return userRepository.findOne(id)
                .map(UserMapper::mapTo)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));
    }

    @Override
    public UserDto create(UserDto request) {
        checkSuchEmail(request.getEmail());
        User user = userRepository.save(UserMapper.mapFrom(request));
        return UserMapper.mapTo(user);
    }

    @Override
    public UserDto patch(Long id, UserDto request) {
        if (request.getEmail() != null) {
            checkSuchEmail(request.getEmail());
        }

        User user = userRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));

        if (request.getName() != null)
            user.setName(request.getName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        return UserMapper.mapTo(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void checkSuchEmail(String email) {
        userRepository.findAll()
                .forEach(user -> {
                    if (user.getEmail().equalsIgnoreCase(email)) {
                        throw new MailAlreadyUserException("Эта почта уже используется");
                    }
                });
    }
}
