package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "bookings", schema = "public")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор бронирования

    @NonNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; // дата и время начала бронирования

    @NonNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; // дата и время конца бронирования

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private final Item item; // вещь, которую пользователь бронирует

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private final User booker; // пользователь, который осуществляет бронирование

    @NonNull
    @Enumerated(EnumType.STRING)
    private StatusBooking status; //  статус бронирования

}