package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findByBookerIdOrderByEndDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByEndDesc(Long bookerId, StatusBooking status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                            LocalDateTime start,
                                                                            LocalDateTime end);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBefore(Long itemId, StatusBooking status,
                                                                 LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfter(Long itemId, StatusBooking status,
                                                                LocalDateTime end, Sort sort);

    @Query("select count(b.id) as count " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and b.booker.id = ?2 " +
            "and b.end < ?3 " +
            "group by b.id ")
    Long isFindBooking(Long itemId, Long userId, LocalDateTime end);

}