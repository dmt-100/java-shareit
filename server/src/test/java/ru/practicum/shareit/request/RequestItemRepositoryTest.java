package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestItemRepositoryTest {
    @Autowired
    TestEntityManager manager;
    @Autowired
    RequestItemRepository repository;
    User owner;
    User requester;
    Item item;
    RequestItem requestItem;
    List<RequestItem> requests;
    List<RequestItem> emptyList;

    static User createOwner() {
        return User.builder()
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    static User createRequester() {
        return User.builder()
                .name("Tim")
                .email("requester@user.com")
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

    static RequestItem createRequest(User requester, Item item) {
        return RequestItem.builder()
                .created(LocalDateTime.now())
                .requester(requester)
                .description("smth")
                .items(List.of(item))
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = createOwner();
        requester = createRequester();
        item = createItem(owner);
        requestItem = createRequest(requester, item);
        manager.persist(owner);
        manager.persist(requester);
        manager.persist(item);
        manager.persist(requestItem);
    }

    @Test
    void findAllByRequesterId() {
        long nonExistedId = requester.getId() + requestItem.getId();
        requests = repository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());
        emptyList = repository.findAllByRequesterIdOrderByCreatedDesc(nonExistedId);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0), equalTo(requestItem));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findRequestPages() {
        PageRequest page = PageRequest.of(0, 10);
        requests = repository.findRequestPages(page, owner.getId());
        emptyList = repository.findRequestPages(page, requester.getId());

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0), equalTo(requestItem));
        assertThat(emptyList.size(), equalTo(0));
    }
}
