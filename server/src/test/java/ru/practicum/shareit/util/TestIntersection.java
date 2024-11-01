package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIntersection {
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start1 = now.plusMinutes(1);
    private final LocalDateTime end1 = now.plusMinutes(2).plusSeconds(1);

    @Test
    public void mustReturnTrueTestIntersection() {
        LocalDateTime start2 = now.plusMinutes(2);
        LocalDateTime end2 = now.plusMinutes(3);
        boolean b = Intersection.timeIntersection(start1, end1, start2, end2);

        assertTrue(b, "Должно вернуться true");
    }

    @Test
    public void mustReturnFalseTestIntersection() {
        LocalDateTime start2 = now.plusMinutes(2).plusSeconds(2);
        LocalDateTime end2 = now.plusMinutes(3);
        boolean b = Intersection.timeIntersection(start1, end1, start2, end2);

        assertFalse(b, "Должно вернуться true");
    }
}
