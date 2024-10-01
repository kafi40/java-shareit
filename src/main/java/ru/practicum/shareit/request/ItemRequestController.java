package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/{id}")
    public ItemRequestDto get(@PathVariable Long id) {
        return itemRequestService.get(id);
    }

    @GetMapping
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId, ItemRequestDto request) {
        return itemRequestService.create(userId, request);
    }

    @PatchMapping
    public ItemRequestDto patch(@RequestHeader("X-Sharer-User-Id") Long userId, ItemRequestDto request) {
        return itemRequestService.patch(userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        itemRequestService.delete(userId, id);
    }
}
