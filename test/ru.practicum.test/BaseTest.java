package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.manager.*;
import ru.practicum.tasks.*;

abstract class BaseTest {
    protected TaskManager taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task task4;

    protected Epic epic1;
    protected Epic epic2;
    protected Epic epic3;
    protected Epic epic4;
    protected Epic epic5;

    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        task1 = taskManager.addTask(new Task("Task", "task"));
        task2 = taskManager.addTask(new Task("Task", "task"));
        task3 = taskManager.addTask(new Task("Task", "task"));
        task4 = taskManager.addTask(new Task("Task", "task"));

        epic1 = taskManager.addEpic(new Epic("Epic", "epic"));
        epic2 = taskManager.addEpic(new Epic("Epic", "epic"));
        epic3 = taskManager.addEpic(new Epic("Epic", "epic"));
        epic4 = taskManager.addEpic(new Epic("Epic", "epic"));
        epic5 = taskManager.addEpic(new Epic("Epic", "epic"));

        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        this.subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        this.subtask2 = taskManager.getSubtaskById(subtaskId2);
    }
}
