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
    void shouldMoveTaskToEndIfCalledAgain() {
        Task task1 = new Task("Task1", "Description1");
        task1.setId(1);
        Task task2 = new Task("Task2", "Description2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(task2, history.get(0), "Вторая задача должна быть первой");
        assertEquals(task1, history.get(1), "Первая задача должна быть второй");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = new Task("Task1", "Description1");
        task1.setId(1);
        Task task2 = new Task("Task2", "Description2");
        task2.setId(2);
        Task task3 = new Task("Task3", "Description3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи");
        assertEquals(List.of(task1, task3), history, "Должны остаться 1 и 3 задачи");
    }

    @Test
    void shouldRemoveHeadAndTailCorrectly() {
        Task task1 = new Task("Task1", "Description1");
        task1.setId(1);
        Task task2 = new Task("Task2", "Description2");
        task2.setId(2);
        Task task3 = new Task("Task3", "Description3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        List<Task> removeHead = historyManager.getHistory();
        assertEquals(List.of(task2, task3), removeHead, "После удления первой задачи должны остаться 2 и 3 задачи");

        historyManager.remove(3);
        List<Task> removeTail = historyManager.getHistory();
        assertEquals(List.of(task2), removeTail, "Должна остаться только 2 задача");
    }

    @Test
    void shouldDoNotRemoveIfIdDoesExist() {
        Task task1 = new Task("Task1", "Description1");
        task1.setId(1);
        historyManager.add(task1);

        historyManager.remove(16);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Должна быть 1 задача");
        assertEquals(task1, history.get(0), "Задача 1 должна быть в истории");
    }
}
