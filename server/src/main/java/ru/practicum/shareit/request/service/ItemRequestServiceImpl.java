package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponse getItemRequest(Long id) {
        log.info("Server: Method getItemRequest begin");
        return itemRequestRepository.findById(id)
                .map(itemRequestMapper::toItemRequestResponse)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponse> getItemRequestForRequestor(Long userId) {
        log.info("Server: Method getItemRequestForRequestor begin");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId);
        return itemRequestMapper.toItemRequestResponseList(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponse> getItemRequestForOther(Long userId) {
        log.info("Server: Method getItemRequestForOther begin");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor_IdNot(userId);
        return itemRequestMapper.toItemRequestResponseList(itemRequests);
    }

    @Override
    public ItemRequestResponse createItemRequest(Long userId, ItemRequestDto request) {
        log.info("Server: Method createItemRequest begin");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(request);
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public ItemRequestResponse patchItemRequest(Long userId, ItemRequestDto request) {
        log.info("Server: Method patchItemRequest begin");
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
        log.info("Server: Method deleteItemRequest begin");
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));

        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        itemRequestRepository.deleteById(id);
    }
}
