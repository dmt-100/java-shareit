package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsByUser(Long userId, Integer from, Integer size);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto saveItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> findItems(Long userId, String text, Integer from, Integer size);

    CommentDto saveComment(Long userId, Long itemId, CommentDto commentDto);

}