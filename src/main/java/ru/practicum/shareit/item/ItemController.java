package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookAndCommentsDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto savedItemDto = itemService.saveItem(itemDto, userId);
        log.info("Сохранена вещь, id = {}", savedItemDto.getId());
        return savedItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        ItemDto savedItemDto = itemService.patchItem(itemDto, userId);
        log.info("Обновлены поля у вещи с id {}", savedItemDto.getId());
        return savedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookAndCommentsDto getItemById(@PathVariable Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Возвращена вещь с id = {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookAndCommentsDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Возвращен список вещей пользователя с id = {}", userId);
        return itemService.getItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text,
                                     @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                     @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
        log.info("Возвращен список всех вещей содеражащих в названии либо описании текст: {} ", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@Valid @RequestBody CommentDto commentDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable(name = "itemId") Long itemId) {
        commentDto.setUserId(userId);
        commentDto.setItemId(itemId);
        CommentDto savedCommentDto = itemService.saveComment(commentDto);
        log.info("Сохранен комментарий для вещи с id = {}", savedCommentDto.getItemId());
        return savedCommentDto;
    }
}
