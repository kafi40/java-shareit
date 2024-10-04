package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentModify;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemModify;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @GetMapping
    public List<ItemResponse> getAllForUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllForUser(userId);
    }

    @PostMapping
    public ItemResponse create(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemModify request) {
        return itemService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemResponse patch(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long id,
                            @RequestBody ItemModify request) {
        return itemService.patch(id, userId, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.delete(id, userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam("text") String text) {
        if (text == null)
            return null;

        return itemService.getForSearch(text.toLowerCase());
    }

    @PostMapping("/{id}/comment")
    public CommentResponse createComment(@Positive @PathVariable Long id,
                                         @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                         @Valid @RequestBody CommentModify request) {
        return itemService.createComment(id, bookerId, request);
    }

}
