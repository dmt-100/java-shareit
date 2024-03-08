package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepositoryTest {
    @Autowired
    TestEntityManager manager;
    @Autowired
    CommentRepository repository;
    Item item;
    List<Comment> comments;
    List<Comment> emptyList;
    User owner;
    User booker;
    Comment comment;

    static User createOwner() {
        return User.builder()
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    static User createBooker() {
        return User.builder()
                .name("Tim")
                .email("booker@user.com")
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

    static Comment createComment(User booker, Item item) {
        return Comment.builder()
                .item(item)
                .created(LocalDateTime.now())
                .author(booker)
                .text("text")
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = createOwner();
        booker = createBooker();
        item = createItem(owner);
        comment = createComment(booker, item);
        manager.persist(owner);
        manager.persist(booker);
        manager.persist(item);
        manager.persist(comment);
    }

    @Test
    void findAllByItemId() {
        long nonExistedId = item.getId() + owner.getId();
        comments = repository.findAllByItemId(item.getId());
        emptyList = repository.findAllByItemId(nonExistedId);

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0), equalTo(comment));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByItemIds() {
        long nonExistedId = item.getId() + owner.getId();
        comments = repository.findAllByItemIds(List.of(item.getId()));
        emptyList = repository.findAllByItemIds(List.of(nonExistedId));

        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0), equalTo(comment));
        assertThat(emptyList.size(), equalTo(0));
    }
}
