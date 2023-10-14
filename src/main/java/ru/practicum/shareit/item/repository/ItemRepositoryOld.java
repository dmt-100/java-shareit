package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryOld {

    List<ItemDto> getAllItemsByUser(Long userId);

    Optional<Item> getItemById(Long itemId);

    Optional<ItemDto> getItemDtoById(Long itemId);

    Optional<ItemDto> saveItem(ItemDto itemDto, User user);

    Optional<ItemDto> updateItem(Long itemId, ItemDto itemDto, User user);

    List<ItemDto> findItems(String text, Long userId);

}