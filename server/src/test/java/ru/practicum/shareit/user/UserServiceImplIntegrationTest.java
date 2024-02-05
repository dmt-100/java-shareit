package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserServiceImpl userService;

    @Test
    @DisplayName("получены все пользователи, когда вызваны, то получен непустой список")
    void getAllUsers() {
        UserDto userDto = makeUserDto("user1", "mail1@mail.ru");
        em.persist(UserMapper.INSTANCE.toUser(userDto));
        userDto = makeUserDto("user2", "mail2@mail.ru");
        em.persist(UserMapper.INSTANCE.toUser(userDto));
        userDto = makeUserDto("user3", "mail3@mail.ru");
        em.persist(UserMapper.INSTANCE.toUser(userDto));

        List<UserDto> users = userService.getAllUsers(); //query.getResultList();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        UserDto user1 = UserMapper.INSTANCE.toUserDto(query1
                .setParameter("email", "mail1@mail.ru")
                .getSingleResult());

        TypedQuery<User> query2 = em.createQuery("Select u from User u where u.email = :email", User.class);
        UserDto user2 = UserMapper.INSTANCE.toUserDto(query2
                .setParameter("email", "mail2@mail.ru")
                .getSingleResult());

        TypedQuery<User> query3 = em.createQuery("Select u from User u where u.email = :email", User.class);
        UserDto user3 = UserMapper.INSTANCE.toUserDto(query3
                .setParameter("email", "mail3@mail.ru")
                .getSingleResult());

        assertThat(users, hasItem(user1));
        assertThat(users, hasItem(user2));
        assertThat(users, hasItem(user3));

        assertThat(users, allOf(hasItem(user1), hasItem(user2), hasItem(user3)));
    }

    @Test
    @DisplayName("сохранен пользователь, когда пользователь валиден, тогда он сохраняется")
    void saveUser() {
        UserDto userDto = makeUserDto("user1", "mail1@mail.ru");
        userService.saveUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);

        return userDto;
    }

}