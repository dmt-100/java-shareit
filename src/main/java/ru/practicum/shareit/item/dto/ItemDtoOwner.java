package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDtoOwner;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDtoOwner extends ItemDto {

    private BookingDtoOwner lastBooking;
    private BookingDtoOwner nextBooking;
    private List<CommentDto> comments;

}