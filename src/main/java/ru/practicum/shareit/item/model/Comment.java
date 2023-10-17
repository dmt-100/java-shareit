package ru.practicum.shareit.item.model;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments", schema = "public")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор комментария

    @Column(nullable = false)
    private String text; // текст комментария

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; //  вещь, к которой относится комментарий

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author; //  автор комментария

    @Column
    private LocalDateTime created; // дата создания комментария

}