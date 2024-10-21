package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long id);

    @Query(" SELECT i FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description)  LIKE LOWER(CONCAT('%', ?1, '%')) )" +
            "AND i.available = TRUE")
    List<Item> findAllByNameOrDescription(String text);
}
