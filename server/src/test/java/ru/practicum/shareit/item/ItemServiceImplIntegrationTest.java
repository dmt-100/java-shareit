package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

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
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;

    @Test
    @DisplayName("получена вещь по ид, когда вещь найдена, тогда она возвращается")
    void getItemById() {
        User user = new User();
        user.setName("user1");
        user.setEmail("mail1@mail.ru");
        em.persist(user);

        ItemDto itemDto = makeItemDto("item1", "item 1", true);
        em.persist(ItemMapper.INSTANCE.toItem(itemDto, user));
        itemDto = makeItemDto("item2", "item 2", false);
        em.persist(ItemMapper.INSTANCE.toItem(itemDto, user));

        ItemDto expectedItemDto = itemService.getItemById(2L, user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query
                .setParameter("id", 2L)
                .getSingleResult();

        assertThat(expectedItemDto.getId(), equalTo(item.getId()));
        assertThat(expectedItemDto.getName(), equalTo(item.getName()));
        assertThat(expectedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(expectedItemDto.getAvailable(), equalTo(item.getAvailable()));
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }

}