package ru.practicum.shareit.util;

import java.time.LocalDateTime;

public interface Intersection {
    static boolean timeIntersection(LocalDateTime s1, LocalDateTime e1, LocalDateTime s2, LocalDateTime e2) {
        return !s1.isBefore(s2) && !s1.isAfter(e2) ||
                e1.isBefore(e2) && e1.isAfter(s2);
    }
}
