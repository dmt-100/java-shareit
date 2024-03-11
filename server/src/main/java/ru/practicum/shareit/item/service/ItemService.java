package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getOwnerItems(long ownerId);

    Item getItem(long id, long ownerId);

    Item addItem(Item item, long ownerId);

    Item updateItem(Item item, long ownerId, long id);

    List<Item> getItemByNameOrDescription(String text);

    Comment addComment(long ownerId, long itemId, Comment comment);
}
