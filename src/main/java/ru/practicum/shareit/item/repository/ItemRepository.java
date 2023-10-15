package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId, Sort sort);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

}