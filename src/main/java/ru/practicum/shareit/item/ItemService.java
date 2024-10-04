package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentModify;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemModify;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {
    ItemResponse get(Long id);

    ItemResponse create(Long userId, ItemModify request);

    ItemResponse patch(Long id, Long userId, ItemModify request);

    void delete(Long id, Long userId);

    List<ItemResponse> getAllForUser(Long id);

    List<ItemResponse> getForSearch(String text);

    CommentResponse createComment(Long itemId, Long bookerId, CommentModify commentModify);
}
