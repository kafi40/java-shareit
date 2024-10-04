package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserModify;
import ru.practicum.shareit.user.dto.UserResponse;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserModify request) {
        return userService.create(request);
    }

    @PatchMapping("/{id}")
    public UserResponse patch(@PathVariable Long id, @RequestBody UserModify request) {
        return userService.patch(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
