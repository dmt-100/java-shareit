package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void findUserByEmail() {
        //given
        userRepository.save(User.builder()
                .email("akhra@yandex.ru")
                .name("Akhra")
                .build());
        //when
        User savedUser = userRepository.findUserByEmail("akhra@yandex.ru").get();
        //then
        assertThat("akhra@yandex.ru", equalTo(savedUser.getEmail()));
    }
}