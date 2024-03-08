package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.item.ItemControllerTest.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemMapperTest {
    Item item;
    ItemDto itemDto;
    ItemResponseDto responseDto;
    ItemShortResponseDto shortResponseDto;

    @Test
    void itemToDto() {
        item = createItem(createOwner());
        itemDto = ItemMapper.itemToDto(item);
        assertNotNull(itemDto);
        assertThat(item.getId(), equalTo(itemDto.getId()));
    }

    @Test
    void itemToResponseDto() {
        item = createItem(createOwner());
        responseDto = ItemMapper.itemToResponseDto(item);
        assertNotNull(responseDto);
        assertThat(item.getId(), equalTo(responseDto.getId()));
    }

    @Test
    void dtoToItem() {
        itemDto = createItemDto();
        item = ItemMapper.dtoToItem(itemDto, new RequestItem());
        assertNotNull(item);
        assertThat(itemDto.getName(), equalTo(item.getName()));
    }

    @Test
    void itemToShortDto() {
        item = createItem(createOwner());
        shortResponseDto = ItemMapper.itemToShortDto(item);
        assertNotNull(shortResponseDto);
        assertThat(shortResponseDto.getId(), equalTo(item.getId()));
    }

    @Test
    void itemsToShortDtoList() {
        item = createItem(createOwner());
        List<ItemShortResponseDto> items = ItemMapper.itemsToShortDtoList(List.of(item));
        assertNotNull(items);
        assertThat(items.size(), equalTo(1));
    }

    @Test
    void itemsToShortDtoListWithWrongArgument() {
        List<ItemShortResponseDto> items = ItemMapper.itemsToShortDtoList(null);
        assertThat(items.size(), equalTo(0));
    }
}
