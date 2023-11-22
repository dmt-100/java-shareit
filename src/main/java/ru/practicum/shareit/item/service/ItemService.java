package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsByUser(Long userId, Integer from, Integer size);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto saveItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    List<ItemDto> findItems(String text, Long userId, Integer from, Integer size);

    CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId);

}