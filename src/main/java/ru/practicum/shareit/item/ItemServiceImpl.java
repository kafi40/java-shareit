package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto get(Long id) {
        return itemRepository.findOne(id)
                .map(ItemMapper::mapTo)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));
    }

    @Override
    public List<ItemDto> getAllForUser(Long id) {
        return itemRepository.findAllForUser(id).stream()
                .map(ItemMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getForSearch(String text) {
        return itemRepository.findForSearch(text).stream()
                .map(ItemMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long userId, ItemDto request) {
        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        Item item = ItemMapper.mapFrom(request);
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.mapTo(item);
    }

    @Override
    public ItemDto patch(Long id, Long userId, ItemDto request) {
        userRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + id + " не найден"));

        Item item = itemRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        ItemMapper.updateField(item, request);
        return ItemMapper.mapTo(item);
    }

    @Override
    public void delete(Long id, Long userId) {
        Item item = itemRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + id + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        itemRepository.delete(id);
    }
}
