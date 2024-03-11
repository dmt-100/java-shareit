package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemController {
    ItemService itemService;
    RequestItemRepository requestItemRepository;

    @GetMapping
    public List<ItemResponseDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getOwnerItems(ownerId).stream()
                .map(ItemMapper::itemToResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                   @PathVariable long id) {
        return ItemMapper.itemToResponseDto(itemService.getItem(id, ownerId));
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                           @RequestBody ItemDto itemDto) {
        RequestItem requestItem = null;
        if (itemDto.getRequestId() != null) {
            requestItem = requestItemRepository.findById(itemDto.getRequestId()).orElse(null);
        }
        return ItemMapper.itemToDto(itemService.addItem(ItemMapper.dtoToItem(itemDto, requestItem), ownerId));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                              @PathVariable long id,
                              @RequestBody ItemDto itemDto) {
        RequestItem requestItem = null;
        if (itemDto.getRequestId() != null) {
            requestItem = requestItemRepository.findById(itemDto.getRequestId()).orElse(null);
        }
        return ItemMapper.itemToDto(itemService.updateItem(ItemMapper.dtoToItem(itemDto, requestItem), ownerId, id));
    }

    @GetMapping("/search")
    public List<ItemDto> findItemByNameOrDescription(@RequestParam String text) {
        return itemService.getItemByNameOrDescription(text)
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PathVariable long itemId,
                                         @RequestBody CommentRequestDto commentDto) {
        return CommentMapper.commentToDto(
                itemService.addComment(ownerId, itemId, CommentMapper.dtoToComment(commentDto)));
    }
}
