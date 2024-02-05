package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId, from, size);
        return ResponseEntity.ok().body(items);
    }

    @GetMapping("/{itemId}")
    /**
     * Получение вещи по id с комментариями
     */
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        return ResponseEntity.ok(itemDto);
    }

    @PostMapping
    @Validated
    /**
     * Добавление новой вещи
     */
    public ResponseEntity<ItemDto> saveItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.saveItem(itemDto, userId);
        return ResponseEntity.ok(itemDto);
    }

    @PatchMapping("/{itemId}")
    /**
     * Редактирование вещи
     */
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId, @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.updateItem(itemId, itemDto, userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/search")
    /**
     * Поиск вещи потенциальным арендатором по тексту
     */
    public ResponseEntity<List<ItemDto>> findItems(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemDto> items = itemService.findItems(text, userId, from, size);
        return ResponseEntity.ok().body(items);
    }

    @PostMapping("/{itemId}/comment")
    @Validated
    /**
     * Добавление комментария к вещи
     */
    public ResponseEntity<CommentDto> saveComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        commentDto = itemService.saveComment(commentDto, itemId, userId);
        return ResponseEntity.ok(commentDto);
    }

}