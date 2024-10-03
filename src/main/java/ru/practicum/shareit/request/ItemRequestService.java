package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto get(Long id);

    List<ItemRequestDto> getAll();

    ItemRequestDto create(Long userId, ItemRequestDto request);

    ItemRequestDto patch(Long id, ItemRequestDto request);

    void delete(Long userId, Long id);
}
