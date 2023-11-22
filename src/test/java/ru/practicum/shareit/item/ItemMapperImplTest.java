package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

class ItemMapperImplTest {

    private final ItemMapperImpl itemMapper = new ItemMapperImpl();

    @Test
    @DisplayName("получен маппер в ДТО вещи, когда вызван нуль, то получен нуль")
    void toItemDtoOwner() {
        ItemDtoOwner itemDtoOwner = itemMapper
                .toItemDtoOwner(null, null, null, null);

        assertThat(itemDtoOwner, nullValue());
    }

    @Test
    @DisplayName("получен маппер в вещь, когда вызван нуль, то получен нуль")
    void toItem() {
        Item item = itemMapper.toItem(null, null);

        assertThat(item, nullValue());
    }

    @Test
    @DisplayName("получен маппер в вещь с запросом, когда вызван нуль, то получен нуль")
    void toItemWithRequest() {
        Item item = itemMapper.toItemWithRequest(null, null, null);

        assertThat(item, nullValue());
    }

    @Test
    @DisplayName("получен маппер в список ДТО вещей, когда вызван нуль, то получен нуль")
    void convertItemListToItemDTOList() {
        List<ItemDto> items = itemMapper.convertItemListToItemDTOList(null);

        assertThat(items, nullValue());
    }

}