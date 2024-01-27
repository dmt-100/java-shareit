package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookAndCommentsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "spring.datasource.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;

    @SneakyThrows
    @Test
    void getItemsOfUser() {
        //given
        UserDto savedUser = userService.saveUser(UserMapper.toUserDto(makeUser("Akhra", "akhra@yandex.ru")));
        List<Item> sourceItems = List.of(
                makeItem("Отвертка", UserMapper.toUser(savedUser)),
                makeItem("Дрель", UserMapper.toUser(savedUser)),
                makeItem("Пылесос", UserMapper.toUser(savedUser))
        );
        sourceItems.forEach(item -> itemService.saveItem(ItemMapper.toItemDto(item), savedUser.getId()));
        int pageSize = 2;
        //when
        List<ItemWithBookAndCommentsDto> targetItems = itemService.getItemsOfUser(savedUser.getId(), 0, pageSize);
        //then
        int min = Integer.min(pageSize, sourceItems.size());
        assertThat(targetItems, hasSize(min));
        for (ItemWithBookAndCommentsDto targetItem : targetItems) {
            assertThat(targetItem.getId(), notNullValue());
            assertThat(sourceItems, hasItem(hasProperty("name", equalTo(targetItem.getName()))));
        }
    }

    private Item makeItem(String itemName, User owner) {
        Item item = new Item();

        item.setName(itemName);
        item.setAvailable(true);
        item.setDescription("cool");
        item.setOwner(owner);
        return item;
    }

    private User makeUser(String name, String Email) {
        User user = new User();
        user.setName(name);
        user.setEmail(Email);
        return user;
    }
}