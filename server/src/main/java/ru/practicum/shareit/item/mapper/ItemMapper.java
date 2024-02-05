package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {CommentMapper.class, ItemRequestMapper.class})
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toItemDto(Item item);

    ItemDtoShort toItemDtoShort(Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "lastBooking", qualifiedByName = "BookingToBookingDtoOwner")
    @Mapping(target = "nextBooking", source = "nextBooking", qualifiedByName = "BookingToBookingDtoOwner")
    @Mapping(target = "comments", source = "comments")
    ItemDtoOwner toItemDtoOwner(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments);

    @Named("BookingToBookingDtoOwner")
    static BookingDtoOwner toBookingDtoOwner(Booking booking) {
        return BookingMapper.INSTANCE.toBookingDtoOwner(booking);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", source = "owner")
    Item toItem(ItemDto itemDto, User owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "description", source = "itemDto.description")
    @Mapping(target = "request", source = "itemRequest")
    @Mapping(target = "owner", source = "owner")
    Item toItemWithRequest(ItemDto itemDto, User owner, ItemRequest itemRequest);

    List<ItemDto> convertItemListToItemDTOList(List<Item> list);

}