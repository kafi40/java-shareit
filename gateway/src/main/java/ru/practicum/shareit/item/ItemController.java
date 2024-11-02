package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Gateway received: Get item with id={}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForUser(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway received: Get items for user with id={}", userId);
        return itemClient.getItemsForUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @Valid @RequestBody ItemDto request) {
        log.info("Gateway received: Create item={} by user with id={}", request, userId);
        return itemClient.createItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @RequestBody ItemDto request) {
        log.info("Gateway received: Patch item ={} with id={} by user with id={}", request, itemId, userId);
        return itemClient.patchItem(itemId, userId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId,
                           @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway received: Delete item with id={} by user with id={}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam("text") String text) {
        if (text.isBlank())
            return ResponseEntity.noContent().build();
        log.info("Gateway received: Get item by text={}", text);
        return itemClient.getBySearch(text.toLowerCase());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @PathVariable Long itemId,
                                         @NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                         @Valid @RequestBody CommentDto request) {
        log.info("Gateway received: Create comment={} for item with id={} by user with id={}", request, itemId, bookerId);
        return itemClient.createComment(itemId, bookerId, request);
    }
}
