package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private List<Item> items;

    @BeforeEach
    void fillDB() {
        User savedUser = userRepository.save(makeUser("Akhra", "akhra@yandex.ru"));
        Item savedItem1 = itemRepository.save(makeItem("Отвертка", "Крестовая", savedUser));
        Item savedItem2 = itemRepository.save(makeItem("Леска", "длинная", savedUser));
        Item savedItem3 = itemRepository.save(makeItem("Пылесос", "мОщный", savedUser));
        items = List.of(savedItem1, savedItem2, savedItem3);
    }

    @Test
    void findAllByNameOrDescription() {
        //when
        List<Item> items1 = itemRepository.findAllByNameOrDescription("лЕс",
                PageRequest.of(0, 10, Sort.by("id").ascending()));
        List<Item> items2 = itemRepository.findAllByNameOrDescription("ТОв",
                PageRequest.of(0, 10, Sort.by("id").ascending()));
        //then
        assertThat(List.of(items.get(1), items.get(2)), equalTo(items1));
        assertThat(List.of(items.get(0)), equalTo(items2));

    }

    private Item makeItem(String itemName, String itemDescription, User owner) {
        Item item = new Item();

        item.setName(itemName);
        item.setAvailable(true);
        item.setDescription(itemDescription);
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