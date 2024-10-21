package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {
    ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest);

    List<ItemRequestResponse> toItemRequestResponseList(List<ItemRequest> itemRequests);

    ItemRequest toItemRequest(ItemRequestDTO itemRequestDTO);
}
