package ru.practicum.test;

import ru.practicum.tasks.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest extends BaseClass {

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Task saved = taskManager.addTask(task);

        assertNotNull(saved, "Задача не найдена.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(saved, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void tasksEqualWhenIdsEqual() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(100);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(100);

        assertEquals(task1, task2, "Задачи с одинаковыми ID должны быть равны.");
    }
}