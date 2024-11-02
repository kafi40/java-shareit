package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestWithItems getItemRequest(Long id);

    List<ItemRequestWithItems> getItemRequestForRequestor(Long userId);

    List<ItemRequestResponse> getItemRequestForOther(Long userId);

    ItemRequestResponse createItemRequest(Long userId, ItemRequestDto request);

    ItemRequestResponse patchItemRequest(Long itemRequestId, Long userId, ItemRequestDto request);

    void deleteItemRequest(Long userId, Long id);
}
