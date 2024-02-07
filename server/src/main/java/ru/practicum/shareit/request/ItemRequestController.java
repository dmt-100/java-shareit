package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    /**
     * Получение списка своих запросов вместе с данными об ответах на них
     */
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestsByUser(userId);
        return ResponseEntity.ok().body(itemRequestDtos);
    }

    @GetMapping("/all")
    /**
     * Получение списка запросов, созданных другими пользователями
     */
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequestsByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from, @RequestParam Integer size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, from, size);
        return ResponseEntity.ok().body(itemRequestDtos);
    }

    @GetMapping("/{requestId}")
    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах
     */
    public ResponseEntity<ItemRequestDto> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(userId, requestId);
        return ResponseEntity.ok(itemRequestDto);
    }

    @PostMapping
    /**
     * Добавление нового запроса вещи
     */
    public ResponseEntity<ItemRequestDto> saveItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto = itemRequestService.saveItemRequest(userId, itemRequestDto);
        return ResponseEntity.ok(itemRequestDto);
    }

}