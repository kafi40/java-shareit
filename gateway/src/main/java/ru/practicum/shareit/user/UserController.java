package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Gate received: Get user with id {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto request) {
        log.info("Gate received: Create new user {}", request);
        return userClient.createUser(request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patch(@PathVariable Long id, @RequestBody UserDto request) {
        log.info("Gate received: Patch user {} with id {}", request, id);
        return userClient.patchUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Gate received: Delete user with id {}", id);
        return userClient.deleteUser(id);
    }
}
