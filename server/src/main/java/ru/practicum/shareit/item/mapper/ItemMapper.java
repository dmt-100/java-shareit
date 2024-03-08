package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public ItemResponseDto itemToResponseDto(Item item) {
        Optional<Booking> last = Optional.ofNullable(item.getLastBooking());
        Optional<Booking> next = Optional.ofNullable(item.getNextBooking());
        List<Comment> comments = item.getComments();
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .lastBooking(last.isPresent() ? BookingMapper.bookingToShortBooking(item.getLastBooking()) : null)
                .nextBooking(next.isPresent() ? BookingMapper.bookingToShortBooking(item.getNextBooking()) : null)
                .comments(!comments.isEmpty() ? comments.stream()
                        .map(CommentMapper::commentToDto)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public Item dtoToItem(ItemDto itemDto, RequestItem requestItem) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(requestItem)
                .build();
    }

    public ItemShortResponseDto itemToShortDto(Item item) {
        return ItemShortResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    public List<ItemShortResponseDto> itemsToShortDtoList(List<Item> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ItemMapper::itemToShortDto)
                .collect(Collectors.toList());
    }
}
