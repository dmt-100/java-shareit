package ru.practicum.shareit.utils;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.S";
    public static final DateTimeFormatter FORMATTER_FOR_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FOR_DATETIME);

}