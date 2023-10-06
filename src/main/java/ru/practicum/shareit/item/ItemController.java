package ru.practicum.shareit.item;

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

    private final ItemService itemService;

    @GetMapping
    /**
     * Просмотр владельцем списка всех его вещей
     */
    public ResponseEntity<List<ItemDto>> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getAllItemsByUser(userId);
        log.info("Получен список вещей пользователя с id = {}, количество = {}.", userId, items.size());
        return ResponseEntity.ok().body(items);
    }

    @GetMapping("/{itemId}")
    /**
     * Получение вещи по id
     */
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        log.info("Получена вещь с id = {}.", itemId);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    @Validated
    /**
     * Добавление новой вещи
     */
    public ResponseEntity<ItemDto> saveItem(@Valid @RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemDtoNew = itemService.saveItem(itemDto, userId);
        log.info("Добавлена новая вещь: {}.", itemDtoNew);
        return ResponseEntity.ok(itemDtoNew);
    }

    @PatchMapping("/{itemId}")
    /**
     * Редактирование вещи
     */
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.updateItem(itemId, itemDto, userId);
        log.info("Обновлена вещь: {}.", itemDto);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/search")
    /**
     * Поиск вещи потенциальным арендатором
     */
    public ResponseEntity<List<ItemDto>> findItems(@RequestParam String text,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.findItems(text, userId);
        log.info("Получен список вещей с текстом: \"{}\" пользователя с id = {}, количество = {}.",
                text, userId, items.size());
        return ResponseEntity.ok().body(items);
    }

}