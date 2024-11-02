package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        log.info("Server received: Get user with id={}", userId);
        return userService.getUser(userId);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserDto request) {
        log.info("Server received: Create user={}", request);
        return userService.createUser(request);
    }

    @PatchMapping("/{userId}")
    public UserResponse patchUser(@PathVariable Long userId, @RequestBody UserDto request) {
        log.info("Server received: Patch user={} with id={}", request, userId);
        return userService.patchUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Server received: Delete user with id={}", userId);
        userService.deleteUser(userId);
    }
}
