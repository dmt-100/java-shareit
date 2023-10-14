package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOtherOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryItemRepository implements ItemRepositoryOld {

    Map<Long, List<Item>> items = new HashMap<>();
    public static long itemId = 0;  // сквозной счетчик вещей

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        return items.get(userId).stream()
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public Optional<ItemDto> getItemDtoById(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .map(ItemMapper.INSTANCE::toItemDto)
                .findFirst();
    }

    @Override
    public Optional<ItemDto> saveItem(ItemDto itemDto, User user) {
        Item item = ItemMapper.INSTANCE.toItem(itemDto, user);
        item.setId(getNextId());

        items.compute(item.getOwner().getId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return Optional.of(ItemMapper.INSTANCE.toItemDto(item));
    }

    @Override
    public Optional<ItemDto> updateItem(Long itemId, ItemDto itemDto, User user) {
        Item item = getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с id = " + itemId + "не найдена."));
        if (!item.getOwner().equals(user)) {
            throw new ItemOtherOwnerException(String.format("Пользователь с id = " + user.getId() +
                    " не является владельцем вещи: " + itemDto));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return Optional.of(ItemMapper.INSTANCE.toItemDto(item));
    }

    @Override
    public List<ItemDto> findItems(String text, Long userId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper.INSTANCE::toItemDto)
                .collect(Collectors.toList());
    }

    private static Long getNextId() {
        return ++itemId;
    }

}