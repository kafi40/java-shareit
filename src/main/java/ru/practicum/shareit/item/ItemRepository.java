package ru.practicum.shareit.item;

import ru.practicum.shareit.base.BaseRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends BaseRepository<Item> {

    List<Item> findAllForUser(Long id);

    List<Item> findForSearch(String text);


}
