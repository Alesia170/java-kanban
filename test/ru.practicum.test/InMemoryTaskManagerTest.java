package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.manager.*;
import ru.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void generatedIdsAreUnique() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
        Task task2 = taskManager.addTask(new Task("Task", "Description1"));

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void taskIdDoesNotConflictWithGeneratedId() {
        Task manualTest = new Task("Name", "Description");
        manualTest.setId(999);
        Task savedManualTask = taskManager.addTask(manualTest);

        Task savedAutoTask = taskManager.addTask(new Task("Name1", "Description1"));

        assertNotEquals(savedManualTask.getId(), savedAutoTask.getId(), "Задачи с ручным ID и " +
                "сгенерированные ID задачи не должны совпадать");

        Task retrievedManual = taskManager.getTaskById(savedManualTask.getId());
        Task retrievedAuto = taskManager.getTaskById(savedAutoTask.getId());

        assertEquals("Name", retrievedManual.getName(), "Имя задачи с ручным ID некорректно");
        assertEquals("Name1", retrievedAuto.getName(), "Имя авто-созданной задачи некорректно");
    }
}