package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private List<Booking> savedBookings;

    @BeforeEach
    void fillDB() {
        User savedUser1 = userRepository.save(makeUser("Akhra", "akhra@yandex.ru"));
        User savedUser2 = userRepository.save(makeUser("Anri", "anri@yandex.ru"));
        Item savedItem1 = itemRepository.save(makeItem("Отвертка", "Крестовая", savedUser1));
        Item savedItem2 = itemRepository.save(makeItem("Леска", "длинная", savedUser1));
        Item savedItem3 = itemRepository.save(makeItem("Пылесос", "мОщный", savedUser2));

        Booking savedBooking1 = bookingRepository.save(makeBooking(savedItem1, savedUser2,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                Status.APPROVED));
        Booking savedBooking2 = bookingRepository.save(makeBooking(savedItem2, savedUser2,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                Status.APPROVED));
        Booking savedBooking3 = bookingRepository.save(makeBooking(savedItem3, savedUser1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4),
                Status.APPROVED));
        savedBookings = List.of(savedBooking1, savedBooking2, savedBooking3);

    }

    @Test
    void findByCurrentBooker() {
        //when
        List<Booking> returnedBookings = bookingRepository.findByCurrentBooker(savedBookings.get(0).getBooker().getId(),
                LocalDateTime.now().plusDays(3),
                PageRequest.of(0, 10, Sort.by("id").ascending()));
        //then
        assertThat(List.of(savedBookings.get(1)), equalTo(returnedBookings));

    }

    @Test
    void findByOwnerCurrentBooker() {
        //when
        List<Booking> returnedBookings = bookingRepository.findByOwnerCurrentBooker(savedBookings.get(2).getItem().getOwner().getId(),
                LocalDateTime.now().plusDays(3),
                PageRequest.of(0, 10, Sort.by("id").ascending()));
        //then
        assertThat(List.of(savedBookings.get(2)), equalTo(returnedBookings));
    }

    @Test
    void findTimeCrossingBookings() {
        //when
        List<Booking> returnedBookings = bookingRepository.findTimeCrossingBookings(savedBookings.get(1).getItem().getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4));
        //then
        assertThat(List.of(savedBookings.get(1)), equalTo(returnedBookings));
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

    private Booking makeBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return booking;
    }

}