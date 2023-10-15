package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    UserDtoShort toUserDtoShort(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDto userDto);

    List<UserDto> convertUserListToUserDTOList(List<User> list);

}