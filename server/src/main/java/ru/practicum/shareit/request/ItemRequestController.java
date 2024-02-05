package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, from, size);
        return ResponseEntity.ok().body(itemRequestDtos);
    }

    @GetMapping("/{requestId}")
    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах
     */
    public ResponseEntity<ItemRequestDto> getItemRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(requestId, userId);
        return ResponseEntity.ok(itemRequestDto);
    }

    @PostMapping
    @Validated
    /**
     * Добавление нового запроса вещи
     */
    public ResponseEntity<ItemRequestDto> saveItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemRequestDto = itemRequestService.saveItemRequest(itemRequestDto, userId);
        return ResponseEntity.ok(itemRequestDto);
    }

}