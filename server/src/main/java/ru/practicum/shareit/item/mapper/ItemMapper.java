package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemShortResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {
    ItemResponse toItemResponse(Item item);

    @Mapping(target = "comments", source = "comments")
    ItemResponse toItemResponseWithComments(Item item, List<CommentResponse> comments);

    List<ItemResponse> toItemResponseList(List<Item> items);

    Item toItem(ItemDto itemDTO);

    ItemShortResponse toItemShortResponse(Item item);

    List<ItemShortResponse> toItemShortResponses(List<Item> items);
}
