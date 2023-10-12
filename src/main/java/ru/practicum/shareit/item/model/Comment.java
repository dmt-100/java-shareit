package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.model.User;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "comments", schema = "public")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор комментария

    @NonNull
    @Column(nullable = false)
    private String text; // текст комментария

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private final Item item; // вещь, на которую автор пишет коммент

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private final User author; // автор, который оставляет коммент

}