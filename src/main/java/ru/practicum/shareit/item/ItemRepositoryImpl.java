package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.base.BaseRepositoryImpl;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl extends BaseRepositoryImpl<Item> implements ItemRepository {
    private final Map<Long, List<Item>> itemCache = new HashMap<>();

    @Override
    public Item save(Item entity) {
        Item item = super.save(entity);
        Long userId = item.getOwner().getId();
        itemCache.computeIfAbsent(userId, k -> new ArrayList<>());
        itemCache.get(entity.getOwner().getId()).add(item);
        return item;
    }

    @Override
    public void delete(Long id) {
        Item item = cache.get(id);
        Long userId = item.getOwner().getId();
        itemCache.get(userId).remove(item);
        if (itemCache.get(userId).isEmpty()) {
            itemCache.remove(userId);
        }
        super.delete(id);
    }

    @Override
    public List<Item> findAllForUser(Long id) {
        return itemCache.get(id);
    }

    @Override
    public List<Item> findForSearch(String text) {
        String regex = "(" + text + ")";
        return cache.values().stream()
                .filter(item -> item.getName().toLowerCase().matches(regex) && item.getAvailable())
                .collect(Collectors.toList());
    }
}
