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
import org.mapstruct.factory.Mappers;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    Map<Long, List<Item>> items = new HashMap<>();
    private long itemId = 0;  // сквозной счетчик вещей
    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        return items.get(userId).stream()
                .map(mapper::toItemDto)
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
                .map(mapper::toItemDto)
                .findFirst();
    }

    @Override
    public Optional<ItemDto> saveItem(ItemDto itemDto, User user) {
        Item item = mapper.toItem(itemDto, user);
        item.setId(getNextId());

        items.compute(item.getOwner().getId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return Optional.of(mapper.toItemDto(item));
    }

    @Override
    public Optional<ItemDto> updateItem(Long itemId, ItemDto itemDto, User user) {
        Item item = getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id = %d не найдена.", itemId)));
        if (!item.getOwner().equals(user)) {
            throw new ItemOtherOwnerException(String.format("Пользователь с id = %d не является владельцем вещи: %s",
                    user.getId(), itemDto));
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

        return Optional.of(mapper.toItemDto(item));
    }

    @Override
    public List<ItemDto> findItems(String text, Long userId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return ++itemId;
    }

}