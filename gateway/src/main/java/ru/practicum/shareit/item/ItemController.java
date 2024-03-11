package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

import static ru.practicum.shareit.util.Headers.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemController {
    ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(USER_ID) long ownerId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get owner items, ownerId = {}, from = {}, size = {}", ownerId, from, size);
        return itemClient.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long ownerId,
                                          @PathVariable long id) {
        log.info("Get item, userId = {}, itemId = {}", ownerId, id);
        return itemClient.getItem(ownerId, id);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) long ownerId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Add item, ownerId = {}, item = {}", ownerId, itemDto);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) long ownerId,
                                             @PathVariable long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Update item, ownerID ={}, itemId = {}", ownerId, id);
        return itemClient.updateItem(ownerId, id, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByNameOrDescription(@RequestHeader(USER_ID) long ownerId,
                                                              @RequestParam String text,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Find item by name or description, userId = {}, text = {}", ownerId, text);
        return itemClient.findItemByNameOrDescription(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long ownerId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Add comment, ownerId = {}, itemId = {}, comment = {}", ownerId, itemId, commentDto);
        return itemClient.addComment(ownerId, itemId, commentDto);
    }
}