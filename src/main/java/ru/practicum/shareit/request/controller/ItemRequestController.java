package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/{id}")
    public ItemRequestResponse get(@PathVariable Long id) {
        return itemRequestService.get(id);
    }

    @GetMapping
    public List<ItemRequestResponse> getAll() {
        return itemRequestService.getAll();
    }

    @PostMapping
    public ItemRequestResponse create(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                    ItemRequestDTO request) {
        return itemRequestService.create(userId, request);
    }

    @PatchMapping
    public ItemRequestResponse patch(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                   ItemRequestDTO request) {
        return itemRequestService.patch(userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long id) {
        itemRequestService.delete(userId, id);
    }
}
