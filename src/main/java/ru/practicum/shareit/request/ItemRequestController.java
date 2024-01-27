package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestsService itemRequestsService;

    @PostMapping
    public ItemRequestOutDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestInDto itemRequestInDto) {
        itemRequestInDto.setUserId(userId);
        ItemRequestOutDto savedItemRequestOutDto = itemRequestsService.addRequest(itemRequestInDto);
        log.info("Добавлен запрос, id = {}", savedItemRequestOutDto.getId());
        return savedItemRequestOutDto;
    }

    @GetMapping
    public List<ItemRequestOutWithItemsDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
//        log.info("Возвращен список всех пользователей");
        return itemRequestsService.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(name = "size", defaultValue = "10") @Min(0) int size) {
//        log.info("Возвращен список всех пользователей");
        return itemRequestsService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutWithItemsDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable(name = "requestId") Long requestId) {
//        log.info("Возвращен список всех пользователей");
        return itemRequestsService.getRequestById(userId, requestId);
    }

}
