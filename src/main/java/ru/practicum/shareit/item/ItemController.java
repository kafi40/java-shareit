package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.HeaderExpectedException;
import ru.practicum.shareit.exception.IdNotCurrentException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemDto> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        if (userId < 1) {
            throw new IdNotCurrentException("ID не может быть меньше 1");
        }

        return itemService.getAllForUser(userId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto request) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        return itemService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @PathVariable Long id,
                         @RequestBody ItemDto request) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        return itemService.patch(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null)
            throw new HeaderExpectedException("Ожидался заголовок \"X-Sharer-User-Id\", но он не найден");

        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        if (text == null)
            return null;

        return itemService.getForSearch(text.toLowerCase());
    }
}
