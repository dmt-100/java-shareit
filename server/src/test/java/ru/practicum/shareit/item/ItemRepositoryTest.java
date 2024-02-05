package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User user = new User();
    private final Item item1 = new Item();
    private final Item item2 = new Item();
    private final Item item3 = new Item();

    @BeforeEach
    public void addItems() {
        user.setName("name");
        user.setEmail("mail@mail.ru");

        item1.setName("1");
        item1.setDescription("ОписАнИе 1");
        item1.setAvailable(true);
        item1.setOwner(user);
        item2.setName("2");
        item2.setDescription("опИсание 2");
        item2.setAvailable(false);
        item2.setOwner(user);
        item3.setName("3");
        item3.setDescription("опИсаниЕ 3");
        item3.setAvailable(true);
        item3.setOwner(user);

        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @AfterEach
    public void deleteItems() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("получены все вещи по ид владельца, когда вызвана, " +
            "то получен список вещей")
    void findAllByOwnerId() {
        List<Item> actualItems = itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(1, 1));

        assertThat(1, equalTo(actualItems.size()));
        assertThat(item2, equalTo(actualItems.get(0)));
    }

    @Test
    @Order(2)
    @DisplayName("получены все вещи по тексту, когда вызвана, то получен список вещей")
    void search() {
        List<Item> actualItems = itemRepository.search("ОПИСАНИ", PageRequest.of(0, 5));

        assertThat(2, equalTo(actualItems.size()));
        assertThat(item1, equalTo(actualItems.get(0)));
        assertThat(item3, equalTo(actualItems.get(1)));
    }

}