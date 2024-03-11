package ru.practicum.shareit.request.model;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String description;
    LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    @ToString.Exclude
    User requester;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    List<Item> items;
}
