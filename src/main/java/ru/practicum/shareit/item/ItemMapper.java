package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;

public class ItemMapper {
    public static ItemDto mapTo(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequestDto(item.getItemRequest() != null ? ItemRequestMapper.mapTo(item.getItemRequest()) : null)
                .build();
    }

    public static Item mapFrom(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .itemRequest(itemDto.getItemRequestDto() != null ? ItemRequestMapper.mapFrom(itemDto.getItemRequestDto()) : null)
                .build();
    }
}
