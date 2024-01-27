package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Методы для получения бронирований сделанных переданным пользователем
    List<Booking> findByBookerId(Long bookerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
    List<Booking> findByCurrentBooker(Long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Pageable page);


    //Методы для получения бронирований вещей владельцем которых являетя переданный пользователь
    List<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
    List<Booking> findByOwnerCurrentBooker(Long ownerId, LocalDateTime start, Pageable page);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime end, Pageable page);

    List<Booking> findByItemOwnerIdAndStatus(Long bookerId, Status status, Pageable page);


    List<Booking> findByItemId(Long itemId, Sort sort);

    List<Booking> findByItemIdIn(List<Long> itemId, Sort sort);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and (b.start between ?2 and ?3 " +
            "or b.end between ?2 and ?3) ")
    List<Booking> findTimeCrossingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

}
