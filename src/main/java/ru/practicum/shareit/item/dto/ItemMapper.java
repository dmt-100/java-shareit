package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingIdOutDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static ItemWithBookAndCommentsDto toItemWithBookAndCommentsDto(Item item, BookingIdOutDto lastBooking, BookingIdOutDto nextBooking, List<CommentDto> comments) {
        ItemWithBookAndCommentsDto itemDto = new ItemWithBookAndCommentsDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDescriptionDto toItemDescriptionDto(Item item) {
        ItemDescriptionDto itemDescriptionDto = new ItemDescriptionDto();
        itemDescriptionDto.setId(item.getId());
        itemDescriptionDto.setName(item.getName());
        itemDescriptionDto.setOwnerId(item.getOwner().getId());
        return itemDescriptionDto;
    }
}
