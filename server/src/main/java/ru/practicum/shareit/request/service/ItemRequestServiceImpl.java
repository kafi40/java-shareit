package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestResponse getItemRequest(Long id) {
        return itemRequestRepository.findById(id)
                .map(itemRequestMapper::toItemRequestResponse)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));
    }

    @Override
    public List<ItemRequestResponse> getItemRequestForRequestor(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);
        return itemRequestMapper.toItemRequestResponseList(itemRequests);
    }

    @Override
    public List<ItemRequestResponse> getItemRequestForOther(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdNot(userId);
        return itemRequestMapper.toItemRequestResponseList(itemRequests);
    }

    @Override
    public ItemRequestResponse createItemRequest(Long userId, ItemRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toItemRequest(request));
        itemRequest.setRequestor(user);
        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public ItemRequestResponse patchItemRequest(Long userId, ItemRequestDto request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        ItemRequest itemRequest = itemRequestRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + request.getId() + " не найден"));

        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        if (request.getDescription() != null) {
            itemRequest.setDescription(request.getDescription());
        }
        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public void deleteItemRequest(Long userId, Long id) {
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));

        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        itemRequestRepository.deleteById(id);
    }
}
