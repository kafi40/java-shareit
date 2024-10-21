package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    ItemResponse toItemResponse(Item item);

    ItemResponse toItemResponseWithComments(Item item, List<Comment> comments);

    List<ItemResponse> toItemResponseList(List<Item> items);

    Item toItem(ItemDTO itemDTO);
}
