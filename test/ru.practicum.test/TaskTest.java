package ru.practicum.test;

import ru.practicum.tasks.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksEqualWhenIdsEqual() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(100);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(100);

        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны.");
        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым ID должны иметь " +
                "одинаковый hashCode");
    }
}