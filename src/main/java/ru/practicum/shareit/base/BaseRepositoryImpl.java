package ru.practicum.shareit.base;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public abstract class BaseRepositoryImpl<T extends Entity> {
    private Long currentId = 0L;
    protected final Map<Long, T> cache = new HashMap<>();

    public Optional<T> findOne(Long id) {
        return Optional.ofNullable(cache.get(id));
    }

    public List<T> findAll() {
        return cache.values().stream().toList();
    }

    public T save(T entity) {
        entity.setId(idGenerator());
        cache.put(entity.getId(), entity);
        return cache.get(entity.getId());
    }

    public T patch(Long id, T entity) {
        cache.put(id, entity);
        return entity;
    }

    public void delete(Long id) {
        cache.remove(id);
    }

    private Long idGenerator() {
        currentId = currentId + 1;
        return currentId;
    }
}
