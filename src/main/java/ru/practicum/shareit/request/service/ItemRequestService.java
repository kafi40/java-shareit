package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse get(Long id);

    List<ItemRequestResponse> getAll();

    ItemRequestResponse create(Long userId, ItemRequestDTO request);

    ItemRequestResponse patch(Long id, ItemRequestDTO request);

    void delete(Long userId, Long id);
}
