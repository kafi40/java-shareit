package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable Long itemRequestId) {
        log.info("Gate received: Get itemRequest for id {}", itemRequestId);
        return itemRequestClient.getItemRequest(itemRequestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestForRequestor(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gate received: Get itemRequests for requestor {}", userId);
        return itemRequestClient.getItemRequestForRequestor(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody ItemRequestDto request) {
        log.info("Gate received: Create itemRequest {} by user with id {}", request, userId);
        return itemRequestClient.createItemRequest(userId, request);
    }

    @PatchMapping
    public ResponseEntity<Object> patchItemRequest(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestDto request) {
        log.info("Gate received: Patch itemRequest {} by user with id {}", request, userId);
        return itemRequestClient.patchItemRequest(userId, request);
    }

    @DeleteMapping("/{itemRequestId}")
    public ResponseEntity<Object> deleteItemRequest(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                       @PathVariable Long itemRequestId) {
        log.info("Gate received: Delete itemRequest {} by user with id {}", itemRequestId, userId);
        return itemRequestClient.deleteItemRequest(userId, itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestForOther(@NotNull @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gate received: Get all other user's itemRequests for user with id {}", userId);
        return itemRequestClient.getItemRequestForOther(userId);
    }
}
