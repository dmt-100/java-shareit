package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    /**
     * Просмотр владельцем списка всех его вещей
     */
    public ResponseEntity<List<ItemDto>> getAllItemsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from, @RequestParam Integer size) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId, from, size);
        return ResponseEntity.ok().body(items);
    }

    @GetMapping("/{itemId}")
    /**
     * Получение вещи по id с комментариями
     */
    public ResponseEntity<ItemDto> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        ItemDto itemDto = itemService.getItemById(userId, itemId);
        return ResponseEntity.ok(itemDto);
    }

    @PostMapping
    /**
     * Добавление новой вещи
     */
    public ResponseEntity<ItemDto> saveItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        itemDto = itemService.saveItem(userId, itemDto);
        return ResponseEntity.ok(itemDto);
    }

    @PatchMapping("/{itemId}")
    /**
     * Редактирование вещи
     */
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        itemDto = itemService.updateItem(userId, itemId, itemDto);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/search")
    /**
     * Поиск вещи потенциальным арендатором по тексту
     */
    public ResponseEntity<List<ItemDto>> findItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text,
            @RequestParam Integer from, @RequestParam Integer size) {
        List<ItemDto> items = itemService.findItems(userId, text, from, size);
        return ResponseEntity.ok().body(items);
    }

    @PostMapping("/{itemId}/comment")
    /**
     * Добавление комментария к вещи
     */
    public ResponseEntity<CommentDto> saveComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        commentDto = itemService.saveComment(userId, itemId, commentDto);
        return ResponseEntity.ok(commentDto);
    }

}