package ru.practicum.shareit.util;

import java.time.LocalDateTime;

public interface Intersection {
    static boolean timeIntersection(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !start1.isBefore(start2) && !start1.isAfter(end2) ||
                end1.isBefore(end2) && end1.isAfter(start2);
    }
}
