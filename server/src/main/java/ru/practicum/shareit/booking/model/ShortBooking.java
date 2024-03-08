package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortBooking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    long bookerId;
}
