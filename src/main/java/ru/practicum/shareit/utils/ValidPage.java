package ru.practicum.shareit.utils;

import ru.practicum.shareit.exception.ValidationException;
import org.springframework.data.domain.PageRequest;

public class ValidPage {

    public static PageRequest validate(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Параметр from не может быть меньше 0.", 10001);
        }
        if (size <= 0) {
            throw new ValidationException("Параметр size должен быть положительным.", 10002);
        }
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

}