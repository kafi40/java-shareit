package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse getItem(long itemId);

    ItemResponse createItem(long userId, ItemDto request);

    ItemResponse patchItem(long itemId, long userId, ItemDto request);

    void deleteItem(long itemId, long userId);

    List<ItemResponse> getAllForUser(long userId);

    List<ItemResponse> getBySearch(String text);

    CommentResponse createComment(long itemId, long bookerId, CommentDto commentDTO);
}
