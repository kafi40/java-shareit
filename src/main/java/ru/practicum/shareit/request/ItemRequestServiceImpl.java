package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto get(Long id) {
        return itemRequestRepository.findOne(id)
                .map(ItemRequestMapper::mapTo)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return itemRequestRepository.findAll().stream()
                .map(ItemRequestMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto request) {
        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapFrom(request));
        itemRequest.setRequestor(user);
        return ItemRequestMapper.mapTo(itemRequest);
    }

    @Override
    public ItemRequestDto patch(Long userId, ItemRequestDto request) {
        userRepository.findOne(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        ItemRequest itemRequest = itemRequestRepository.findOne(request.getId())
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + request.getId() + " не найден"));

        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        if (request.getDescription() != null) {
            itemRequest.setDescription(request.getDescription());
        }
        return ItemRequestMapper.mapTo(itemRequest);
    }

    @Override
    public void delete(Long userId, Long id) {
        ItemRequest itemRequest = itemRequestRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Запрашиваемый предмет с ID = " + id + " не найден"));

        if (!itemRequest.getRequestor().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        itemRequestRepository.delete(id);
    }
}
