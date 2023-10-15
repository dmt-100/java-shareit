package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * TODO Sprint add-item-requests.
 */

// класс, отвечающий за запрос вещи
@Entity
@Table(name = "requests", schema = "public")
@Data
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  уникальный идентификатор запроса

    @Column(nullable = false)
    private String description; // текст запроса, содержащий описание требуемой вещи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    private User requestor; // пользователь, создавший запрос

    @Column
    private Instant created = Instant.now(); // дата и время создания запроса

}