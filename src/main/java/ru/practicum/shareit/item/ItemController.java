package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public List<ItemDto> getAllForUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllForUser(userId);
    }

    @PostMapping
    public ItemDto create(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto request) {
        return itemService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemDto patch(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                         @PathVariable Long id,
                         @RequestBody ItemDto request) {
        return itemService.patch(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        if (text == null)
            return null;

        return itemService.getForSearch(text.toLowerCase());
    }
}
