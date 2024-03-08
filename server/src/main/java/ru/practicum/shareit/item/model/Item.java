package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String name;
    String description;
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    RequestItem request;

    @Transient
    Booking lastBooking;

    @Transient
    Booking nextBooking;

    @Transient
    List<Comment> comments;

}
