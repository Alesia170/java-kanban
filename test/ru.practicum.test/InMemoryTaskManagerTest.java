package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.tasks.Status.*;

class InMemoryTaskManagerTest extends BaseTest {

    @Test
    void addDifferentTypesTasksAndFindById() {
        assertTrue(task1.getId() > 0);
        assertTrue(epic1.getId() > 0);
        assertTrue(subtask1.getId() > 0);

        assertEquals("Task", taskManager.getTaskById(task1.getId()).getName());
        assertEquals("Epic", taskManager.getEpicById(epic1.getId()).getName());
        assertEquals("Subtask", taskManager.getSubtaskById(subtask1.getId()).getName());
    }

    @Test
    void generatedIdsAreUnique() {

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void taskStayTheSameAfterAddManager() {
        Task original = new Task("Name", "Description");
        Task saved = taskManager.addTask(original);

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

    @Test
    void shouldGetAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(4, tasks.size(), "Ожидается 4 задачи в списке");
        assertTrue(tasks.contains(task1), "Список должен содержать первую задачу");
        assertTrue(tasks.contains(task2), "Список должен содержать вторую задачу");
    }

    @Test
    void shouldGetAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(5, epics.size(), "Ожидается 5 эпиков в списке");
        assertTrue(epics.contains(epic1), "Список должен содержать первый эпик");
        assertTrue(epics.contains(epic2), "Список должен содержать первый эпик");
    }

    @Test
    void shouldGetAllSubtasks() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(2, subtasks.size(), "Ожидается 2 подзадачи в списке");
        assertTrue(subtasks.contains(subtask1), "Список должен содержать первую подзадачу");
        assertTrue(subtasks.contains(subtask2), "Список должен содержать первую подзадачу");
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.removeTask(task1.getId());
        taskManager.removeTask(task2.getId());
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "После удаления 2 задач должно остаться 2 задачи");
    }

    @Test
    void shouldRemoveEpicById() {
        taskManager.removeEpic(epic1.getId());
        taskManager.removeEpic(epic2.getId());

        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasksAfterRemove = taskManager.getAllSubtasks();
        assertEquals(3, epics.size(), "После удаления 2 эпиков должно остаться 3 эпика");
        assertEquals(0, subtasksAfterRemove.size(), "Подзадачи удаленного эпика тоже должны удалиться");
    }

    @Test
    void shouldRemoveSubtaskById() {
        taskManager.removeSubtask(subtask1.getId());
        taskManager.removeSubtask(subtask2.getId());

        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(0, subtasks.size(), "После удаления 2 подзадач должно остатсься 0 подзадач");
    }

    @Test
    void shouldClearAllTasks() {
        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldClearAllEpics() {
        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldClearAllSubtasks() {
        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldUpdateTaskCorrectly() {
        Task updated = new Task("Updated", "Updated Description");
        updated.setId(task1.getId());
        updated.setStatus(DONE);

        taskManager.updateTask(updated);
        Task result = taskManager.getTaskById(task1.getId());

        assertEquals("Updated", result.getName(), "Имя должно совпадать");
        assertEquals("Updated Description", result.getDescription(), "Описание должно совпадать");
        assertEquals(DONE, result.getStatus(), "Статус задачи должен обновиться");
    }

    @Test
    void shouldUpdateEpicCorrectly() {
        subtask1.setStatus(DONE);
        subtask2.setStatus(DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        Epic result = taskManager.getEpicById(epic1.getId());

        assertEquals(DONE, result.getStatus(), "Статус подзадачи должен обновиться");
    }

    @Test
    void shouldUpdateSubtaskCorrectly() {
        Subtask updated = new Subtask("Updated", "Updated Description", epic1.getId());
        updated.setId(subtask1.getId());
        updated.setStatus(DONE);

        taskManager.updateSubtask(updated);
        Subtask result = taskManager.getSubtaskById(subtask1.getId());

        assertEquals("Updated", result.getName(), "Имя подзадачи должны обновиться");
        assertEquals("Updated Description", result.getDescription(), "Описание подзадачи должно обновиться");
        assertEquals(DONE, result.getStatus(), "Статус подзадачи должен обновиться");
    }

    @Test
    void epicStatusShouldBeInProgress() {
        subtask1.setStatus(DONE);
        subtask2.setStatus(NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        Epic result = taskManager.getEpicById(epic1.getId());

        assertEquals(IN_PROGRESS, result.getStatus(), "Если одно задача выполнена, а вторая нет, то статус Эпика должен быть в процессе");
    }

    @Test
    void shouldUpdateEpicFields() {
        Epic updated = new Epic("Changed name", "Changed description");
        updated.setId(epic1.getId());

        taskManager.updateEpic(updated);
        Epic result = taskManager.getEpicById(epic1.getId());

        assertEquals("Changed name", result.getName());
        assertEquals("Changed description", result.getDescription());
    }

    @Test
    void shouldRemoveNotActualSubtasks() {
        int subtaskId = subtask1.getId();
        taskManager.removeSubtask(subtaskId);

        assertFalse(epic1.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void shouldRemoveEpicAndAllItsSubtasks() {
        int epicId = epic1.getId();
        int subtaskId1 = subtask1.getId();
        int subtaskId2 = subtask2.getId();

        taskManager.removeEpic(epicId);

        assertNull(taskManager.getEpicById(epicId), "Эпик должен быть удален");
        assertNull(taskManager.getSubtaskById(subtaskId1), "1 Подзадача эпика должна быть удалена");
        assertNull(taskManager.getSubtaskById(subtaskId2), "2 Подзадача эпика должна быть удалена");
    }

    @Test
    void shouldEpicStayTheSameAfterChangingManager() {
        Epic original = new Epic("Name", "Description");
        Epic saved = taskManager.addEpic(original);

        assertEquals("Name", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(NEW, saved.getStatus());
        assertTrue(saved.getId() > 0);

        original.setName("Change Name");
        original.setDescription("Change Description");
        original.setStatus(DONE);

        Epic fromManager = taskManager.getEpicById(saved.getId());

        assertEquals("Name", fromManager.getName());
        assertEquals("Description", fromManager.getDescription());
        assertEquals(NEW, fromManager.getStatus());
    }

    @Test
    void shouldSubtaskStayTheSameAfterChangingManager() {
        Subtask original = new Subtask("Name", "Description", epic1.getId());
        int id = taskManager.addSubtask(original);
        Subtask saved = taskManager.getSubtaskById(id);

        assertEquals("Name", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(NEW, saved.getStatus());
        assertTrue(saved.getId() > 0);

        original.setName("Change Name");
        original.setDescription("Change Description");
        original.setStatus(DONE);

        Subtask fromManager = taskManager.getSubtaskById(saved.getId());

        assertEquals("Name", fromManager.getName());
        assertEquals("Description", fromManager.getDescription());
        assertEquals(NEW, fromManager.getStatus());
    }
}