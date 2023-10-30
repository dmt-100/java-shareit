package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final String xSharerUserId = "X-Sharer-User-Id";
    private final ItemService itemService;

    /**
     * Просмотр владельцем списка всех его вещей
     */
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByUser(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId, from, size);
        log.info("Получен список вещей пользователя с id = {}, количество = {}.", userId, items.size());
        return ResponseEntity.ok().body(items);
    }

    /**
     * Получение вещи по id с комментариями
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable Long itemId,
            @RequestHeader(xSharerUserId) Long userId) {
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        log.info("Получена вещь с id = {}.", itemId);
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Добавление новой вещи
     */
    @PostMapping
    @Validated
    public ResponseEntity<ItemDto> saveItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(xSharerUserId) Long userId) {
        itemDto = itemService.saveItem(itemDto, userId);
        log.info("Добавлена новая вещь: {}.", itemDto);
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Редактирование вещи
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId, @RequestBody ItemDto itemDto,
            @RequestHeader(xSharerUserId) Long userId) {
       itemDto = itemService.updateItem(itemId, itemDto, userId);
        log.info("Обновлена вещь: {}.", itemDto);
        return ResponseEntity.ok(itemDto);
    }

    /**
     * Поиск вещи потенциальным арендатором
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> findItems(
            @RequestParam String text,
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemDto> items = itemService.findItems(text, userId, from, size);
        log.info("Получен список вещей с текстом: {} пользователя с id = {}, количество = {}.",
                text, userId, items.size());
        return ResponseEntity.ok().body(items);
    }

    /**
     * Добавление комментария к вещи
     */
    @PostMapping("/{itemId}/comment")
    @Validated
    public ResponseEntity<CommentDto> saveComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader(xSharerUserId) Long userId,
            @PathVariable Long itemId) {
        commentDto = itemService.saveComment(commentDto, itemId, userId);
        log.info("Добавлен новый комментарий: {} \n пользователем с id = {} для вещи с id = {}.",
                commentDto, userId, itemId);
        return ResponseEntity.ok(commentDto);
    }

}