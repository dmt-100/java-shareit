package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final String xSharerUserId = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping
    /**
     * Получение списка своих запросов вместе с данными об ответах на них
     */
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequestsByUser(
            @RequestHeader(xSharerUserId) Long userId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestsByUser(userId);
        log.info("Получен список запросов текущего пользователя вместе с данными об ответах " +
                "на них с id = {}, количество = {}.", userId, itemRequestDtos.size());
        return ResponseEntity.ok().body(itemRequestDtos);
    }

    @GetMapping("/all")
    /**
     * Получение списка запросов, созданных другими пользователями
     */
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequestsByOtherUsers(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, from, size);
        log.info("Получен список запросов пользователя с id = {}, созданных другими, " +
                "количество = {}.", userId, itemRequestDtos.size());
        return ResponseEntity.ok().body(itemRequestDtos);
    }

    @GetMapping("/{requestId}")
    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах
     */
    public ResponseEntity<ItemRequestDto> getItemRequestById(
            @PathVariable Long requestId,
            @RequestHeader(xSharerUserId) Long userId) {
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Получен запрос с id = {}.", requestId);
        return ResponseEntity.ok(itemRequestDto);
    }

    @PostMapping
    @Validated
    /**
     * Добавление нового запроса вещи
     */
    public ResponseEntity<ItemRequestDto> saveItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(xSharerUserId) Long userId) {
        itemRequestDto = itemRequestService.saveItemRequest(itemRequestDto, userId);
        log.info("Добавлен новый запрос на бронирование: {}", itemRequestDto);
        return ResponseEntity.ok(itemRequestDto);
    }

}