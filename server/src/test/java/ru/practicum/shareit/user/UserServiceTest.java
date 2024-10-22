package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

public class UserServiceTest {

    @Test
    public void testGetUser() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        UserService userService = new UserServiceImpl(mockUserRepository, userMapper);

        UserDto userDto = new UserDto();
        userDto.setEmail("test@ya.ru");

        UserResponse userResponse = userService.createUser(userDto);
        System.out.println(userResponse);
    }
}
