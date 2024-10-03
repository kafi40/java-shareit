package ru.practicum.shareit.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {

    Optional<T> findOne(Long id);

    List<T> findAll();

    T save(T entity);

    T patch(Long id, T entity);

    void delete(Long id);
}
