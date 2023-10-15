package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsBefore(Long itemId, LocalDateTime end, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsAfter(Long itemId, LocalDateTime end, Sort sort);

}