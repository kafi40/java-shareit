package ru.practicum.shareit.util;

import java.time.LocalDateTime;

@FunctionalInterface
public interface Crossable {
    boolean isCross(LocalDateTime start, LocalDateTime end);
}
