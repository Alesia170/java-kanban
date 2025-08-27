package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.*;
import ru.practicum.tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTasksToEnd() {
        Task task1 = new Task("Task1", "Description1");
        task1.setId(1);
        Task task2 = new Task("Task2", "Description2");
        task2.setId(2);
        Epic epic1 = new Epic("Epic1", "Description1");
        epic1.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task1, history.get(0), "Первая задача должна быть добавлена первой");
        assertEquals(task2, history.get(1), "Вторая задача должна быть добавлена второй");
        assertEquals(epic1, history.get(2), "Эпик должен быть добавлен третий");
    }

    @Test
    void shouldLimitHistoryToTenTasks() {
        for (int i = 0; i <= 13; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "История должна содержать 10 задач");
        assertEquals("Task 4", history.get(0).getName(), "Первой задачей должна быть 4 задача");
        assertEquals("Task 13", history.get(9).getName(), "Последней задаче должна быть 13 задача");
    }
}
