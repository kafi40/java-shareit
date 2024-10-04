package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestModify;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse get(Long id);

    List<ItemRequestResponse> getAll();

    ItemRequestResponse create(Long userId, ItemRequestModify request);

    ItemRequestResponse patch(Long id, ItemRequestModify request);

    void delete(Long userId, Long id);
}
