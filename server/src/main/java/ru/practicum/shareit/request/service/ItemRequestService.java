package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse getItemRequest(Long id);

    List<ItemRequestResponse> getItemRequestForRequestor(Long userId);

    List<ItemRequestResponse> getItemRequestForOther(Long userId);

    ItemRequestResponse createItemRequest(Long userId, ItemRequestDto request);

    ItemRequestResponse patchItemRequest(Long id, ItemRequestDto request);

    void deleteItemRequest(Long userId, Long id);
}
