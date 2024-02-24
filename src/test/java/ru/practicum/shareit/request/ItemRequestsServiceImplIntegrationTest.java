package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;
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
class ItemRequestsServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemRequestsService itemRequestsService;

    @Test
    void getAllRequests() {
        //given
        UserDto savedUser1 = userService.saveUser(UserMapper.toUserDto(createUser("Akhra", "akhra@yandex.ru")));
        UserDto savedUser2 = userService.saveUser(UserMapper.toUserDto(createUser("Anri", "anri@yandex.ru")));
        List<ItemRequest> sourceRequests = List.of(
                makeItemRequest("Отвертка", UserMapper.toUser(savedUser1)),
                makeItemRequest("Дрель", UserMapper.toUser(savedUser1)),
                makeItemRequest("Пылесос", UserMapper.toUser(savedUser1))
        );
        sourceRequests.forEach(request -> itemRequestsService.addRequest(ItemRequestMapper.mapToItemRequestInDto(request)));
        int pageSize = 2;
        //when
        List<ItemRequestOutWithItemsDto> targetRequests = itemRequestsService.getAllRequests(savedUser2.getId(), 0, pageSize);
        //then
        int min = Integer.min(pageSize, sourceRequests.size());
        assertThat(targetRequests, hasSize(min));
        for (ItemRequestOutWithItemsDto targetRequest : targetRequests) {
            assertThat(targetRequest.getId(), notNullValue());
            assertThat(sourceRequests, hasItem(hasProperty("description", equalTo(targetRequest.getDescription()))));
        }
    }

    private ItemRequest makeItemRequest(String name, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(name);
        request.setRequester(requester);
        return request;
    }

    private User createUser(String name, String Email) {
        User user = new User();
        user.setName(name);
        user.setEmail(Email);
        return user;
    }
}