package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse get(Long id);

    ItemResponse create(Long userId, ItemDTO request);

    ItemResponse patch(Long id, Long userId, ItemDTO request);

    void delete(Long id, Long userId);

    List<ItemResponse> getAllForUser(Long id);

    List<ItemResponse> getForSearch(String text);

    CommentResponse createComment(Long itemId, Long bookerId, CommentDTO commentDTO);
}
