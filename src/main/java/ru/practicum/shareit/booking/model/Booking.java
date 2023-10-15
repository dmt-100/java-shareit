package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "bookings", schema = "public")
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор бронирования

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; // дата и время начала бронирования

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; // дата и время конца бронирования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item; // вещь, которую пользователь бронирует

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    @ToString.Exclude
    private User booker; // пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    private StatusBooking status; //  статус бронирования

}