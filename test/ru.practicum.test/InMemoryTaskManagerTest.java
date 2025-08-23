package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.tasks.Status.*;

class InMemoryTaskManagerTest extends BaseClass {

    @Test
    void addDifferentTypesTasksAndFindById() {
        Task task = new Task("Task", "task");
        Task taskSaved = taskManager.addTask(task);
        Epic epic = new Epic("Epic", "epic");
        Epic epicSaved = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask", epic.getId());
        Subtask subtaskSaved = taskManager.addSubtask(subtask);

        assertTrue(taskSaved.getId() > 0);
        assertTrue(epicSaved.getId() > 0);
        assertTrue(subtaskSaved.getId() > 0);

        assertEquals("Task", taskManager.getTaskById(task.getId()).getName());
        assertEquals("Epic", taskManager.getEpicById(epic.getId()).getName());
        assertEquals("Subtask", taskManager.getSubtaskById(subtask.getId()).getName());
    }

    @Test
    void generatedIdsAreUnique() {
        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");
        task1 = taskManager.addTask(task1);
        task2 = taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void taskStayTheSameAfterAddManager() {
        Task original = new Task("Name", "Description");
        Task saved = taskManager.addTask(original);

        assertNotNull(saved);

        assertEquals("Name", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(NEW, saved.getStatus());
        assertTrue(saved.getId() > 0);

        original.setName("Change Name");
        original.setDescription("Change Description");
        original.setStatus(DONE);

        Task fromManager = taskManager.getTaskById(saved.getId());
        assertEquals("Name", fromManager.getName());
        assertEquals("Description", fromManager.getDescription());
        assertEquals(NEW, fromManager.getStatus());
    }

    @Test
    void taskIdDoesNotConflictWithGeneratedId() {
        Task manualTask = new Task("Name", "Description");
        manualTask.setId(999);
        Task savedManualTask = taskManager.addTask(manualTask);

        Task autoTask = new Task("Name1", "Description1");
        Task savedAutoTask = taskManager.addTask(autoTask);

        assertNotEquals(savedManualTask.getId(), savedAutoTask.getId(), "Задачи с ручным ID и " +
                "сгенерированные ID задачи не должны совпадать");

        Task retrievedManual = taskManager.getTaskById(savedManualTask.getId());
        Task retrievedAuto = taskManager.getTaskById(savedAutoTask.getId());

        assertEquals("Name", retrievedManual.getName(), "Имя задачи с ручным ID некорректно");
        assertEquals("Name1", retrievedAuto.getName(), "Имя авто-созданной задачи некорректно");
    }
}