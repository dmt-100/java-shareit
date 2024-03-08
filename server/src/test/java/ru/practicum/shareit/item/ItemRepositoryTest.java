package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRepositoryTest {
    @Autowired
    TestEntityManager manager;
    @Autowired
    ItemRepository repository;
    Item item;
    List<Item> items;
    List<Item> emptyList;
    User owner;

    static User createOwner() {
        return User.builder()
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .name("pen")
                .description("smth")
                .available(true)
                .owner(owner)
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = createOwner();
        item = createItem(owner);
        manager.persist(owner);
        manager.persist(item);
    }

    @Test
    void findAllByOwnerId() {
        long nonExistedId = item.getId() + owner.getId();
        items = repository.findAllByOwnerIdOrderById(owner.getId());
        emptyList = repository.findAllByOwnerIdOrderById(nonExistedId);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findByNameOrDescription() {
        items = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                "pen", "smth", true
        );
        emptyList = repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                "asdf", "asdf", true
        );
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(item));
        assertThat(emptyList.size(), equalTo(0));
    }
}
