package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {

    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", source = "requestor")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor);

    List<ItemRequestDto> convertItemRequestListToItemRequestDTOList(List<ItemRequest> list);

}