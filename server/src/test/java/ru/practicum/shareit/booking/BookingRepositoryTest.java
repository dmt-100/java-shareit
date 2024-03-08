package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingRepositoryTest {
    @Autowired
    TestEntityManager manager;
    @Autowired
    BookingRepository repository;
    User owner;
    User booker;
    Item item;
    Booking booking;
    List<Booking> bookings;
    List<Booking> emptyList;
    PageRequest page = PageRequest.of(0, 3);

    static User createOwner() {
        return User.builder()
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    public static User createBooker() {
        return User.builder()
                .name("Sam")
                .email("booker@booker.com")
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

    static Booking createBooking(Item item, User booker) {
        return Booking.builder()
                .status(Status.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(10))
                .item(item)
                .booker(booker)
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = createOwner();
        booker = createBooker();
        item = createItem(owner);
        booking = createBooking(item, booker);
        manager.persist(owner);
        manager.persist(booker);
        manager.persist(item);
        manager.persist(booking);
    }

    @Test
    void findAllByBookerId() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdOrderByStartDesc(booker.getId(), page);
        emptyList = repository.findAllByBookerIdOrderByStartDesc(nonExistedId, page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), page);
        emptyList = repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                nonExistedId, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), LocalDateTime.now().plusHours(11), page);
        emptyList = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                nonExistedId, LocalDateTime.now().plusHours(11), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now().minusMinutes(2), page);
        emptyList = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                nonExistedId, LocalDateTime.now().minusMinutes(2), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), Status.WAITING, page);
        emptyList = repository.findAllByBookerIdAndStatusOrderByStartDesc(
                nonExistedId, Status.WAITING, page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByBookerIdAndItemIdAndEndBefore() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByBookerIdAndItemIdAndEndBefore(
                booker.getId(), item.getId(), LocalDateTime.now().plusHours(11));
        emptyList = repository.findAllByBookerIdAndItemIdAndEndBefore(
                nonExistedId, ++nonExistedId, LocalDateTime.now().plusHours(11));

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerId() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByOwnerIdOrderByStartDesc(owner.getId(), page);
        emptyList = repository.findAllByOwnerIdOrderByStartDesc(nonExistedId, page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndStartBeforeAndEndAfter() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                owner.getId(), LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), page);
        emptyList = repository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                nonExistedId, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(2), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndEndBefore() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(
                owner.getId(), LocalDateTime.now().plusHours(11), page);
        emptyList = repository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(
                nonExistedId, LocalDateTime.now().plusHours(11), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndStartAfter() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                owner.getId(), LocalDateTime.now().minusMinutes(2), page);
        emptyList = repository.findAllByOwnerIdAndStartAfterOrderByStartDesc(
                nonExistedId, LocalDateTime.now().minusMinutes(2), page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndStatus() {
        bookings = repository.findAllByOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), Status.WAITING, page);
        emptyList = repository.findAllByOwnerIdAndStatusOrderByStartDesc(
                owner.getId(), Status.APPROVED, page);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndItemIds() {
        List<Long> ids = List.of(item.getId());
        bookings = repository.findAllByOwnerIdAndItemIds(
                owner.getId(), ids);
        emptyList = repository.findAllByOwnerIdAndItemIds(
                owner.getId(), Collections.emptyList());

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }

    @Test
    void findAllByOwnerIdAndItemId() {
        long nonExistedId = booker.getId() + owner.getId();
        bookings = repository.findAllByOwnerIdAndItemId(
                owner.getId(), item.getId());
        emptyList = repository.findAllByOwnerIdAndItemId(
                nonExistedId, ++nonExistedId);

        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0), equalTo(booking));
        assertThat(emptyList.size(), equalTo(0));
    }
}