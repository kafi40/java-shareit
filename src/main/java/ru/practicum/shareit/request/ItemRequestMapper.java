package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

public class ItemRequestMapper {
    public static ItemRequestDto mapTo(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(UserMapper.mapTo(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest mapFrom(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(UserMapper.mapFrom(itemRequestDto.getRequestor()))
                .created(itemRequestDto.getCreated())
                .build();
    }
}
