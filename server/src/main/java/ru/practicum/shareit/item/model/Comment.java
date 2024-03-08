package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String text;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    User author;

    LocalDateTime created;
}
