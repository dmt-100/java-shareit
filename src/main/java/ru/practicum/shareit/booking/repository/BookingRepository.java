package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    Page<Booking> findByBookerIdOrderByEndDesc(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByEndDesc(
            Long bookerId, StatusBooking status, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, PageRequest page);

    Page<Booking> findByBookerIdAndEndIsAfter(
            Long bookerId, LocalDateTime end, PageRequest page);

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