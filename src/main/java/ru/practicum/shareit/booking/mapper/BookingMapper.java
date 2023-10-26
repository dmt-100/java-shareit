package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingOutDto toBookingOutDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDtoOwner toBookingDtoOwner(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    Booking toBooking(BookingInDto bookingDto, User booker, Item item);

    List<BookingOutDto> convertBookingListToBookingOutDTOList(List<Booking> list);

}