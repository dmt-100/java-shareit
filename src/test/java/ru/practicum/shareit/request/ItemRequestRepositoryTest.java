package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
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
class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private List<ItemRequest> savedRequests;

    @BeforeEach
    void fillDB() {
        User savedUser1 = userRepository.save(makeUser("Akhra", "akhra@yandex.ru"));
        User savedUser2 = userRepository.save(makeUser("Anri", "anri@yandex.ru"));
        ItemRequest savedRequest1 = itemRequestRepository.save(makeRequest(savedUser1, "нужна дрель", LocalDateTime.now().plusDays(2)));
        ItemRequest savedRequest2 = itemRequestRepository.save(makeRequest(savedUser1, "нужна отвертка", LocalDateTime.now().plusDays(5)));
        ItemRequest savedRequest3 = itemRequestRepository.save(makeRequest(savedUser2, "нужен перфоратор", LocalDateTime.now().plusDays(5)));
        savedRequests = List.of(savedRequest1, savedRequest2, savedRequest3);
    }


    @Test
    void findByRequesterId() {
        //when
        List<ItemRequest> returnedRequest = itemRequestRepository.findByRequesterId(savedRequests.get(0).getRequester().getId(),
                Sort.by("created").descending());
        //then
        assertThat(List.of(savedRequests.get(1), savedRequests.get(0)), equalTo(returnedRequest));
    }


    private User makeUser(String name, String Email) {
        User user = new User();
        user.setName(name);
        user.setEmail(Email);
        return user;
    }

    private ItemRequest makeRequest(User requester, String description, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription(description);
        itemRequest.setCreated(created);
        return itemRequest;
    }

}