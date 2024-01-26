package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    private List<Comment> savedComments;

    @BeforeEach
    void fillDB() {
        User savedUser = userRepository.save(makeUser("Akhra", "akhra@yandex.ru"));
        Item savedItem1 = itemRepository.save(makeItem("Отвертка", "Крестовая", savedUser));
        Item savedItem2 = itemRepository.save(makeItem("Леска", "длинная", savedUser));
        Comment savedComment1 = commentRepository.save(makeComment(savedItem1, savedUser, "круто!"));
        Comment savedComment2 = commentRepository.save(makeComment(savedItem2, savedUser, "то что нужно!"));
        savedComments = List.of(savedComment1, savedComment2);
    }

    @Test
    void findByItemId() {
        //when
        List<Comment> returnedComments = commentRepository.findByItemId(savedComments.get(0).getItem().getId());
        //then
        assertThat(List.of(savedComments.get(0)), equalTo(returnedComments));
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

    private Comment makeComment(Item item, User user, String text) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setUser(user);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}