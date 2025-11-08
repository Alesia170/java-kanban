package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.practicum.tasks.Status.DONE;
import static ru.practicum.tasks.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void shouldAllStatusSubtasksNew() {
        Epic epic1 = taskManager.addEpic(new Epic("Name", "Description"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask1);

        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).getStatus(),
                "Статус эпика должен быть NEW, так как его подзадачи NEW");
    }

    @Test
    void shouldAllStatusSubtasksDone() {
        Epic epic1 = taskManager.addEpic(new Epic("Name", "Description"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).getStatus(),
                "Статус эпика должен быть DONE, так как его подзадачи DONE");
    }

    @Test
    void shouldStatusSubtasksDoneAndNew() {
        Epic epic1 = taskManager.addEpic(new Epic("Name", "Description"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, так как одна подзадача DONE, другая NEW");
    }

    @Test
    void shouldAllStatusSubtasksInProgress() {
        Epic epic1 = taskManager.addEpic(new Epic("Name", "Description"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, так как его подзадачи все IN_PROGRESS");
    }

    @Test
    void addDifferentTypesTasksAndFindById() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        assertTrue(task1.getId() > 0);
        assertTrue(epic1.getId() > 0);
        assertTrue(subtask1.getId() > 0);

        assertEquals("Task", taskManager.getTaskById(task1.getId()).getName());
        assertEquals("Epic", taskManager.getEpicById(epic1.getId()).getName());
        assertEquals("Subtask", taskManager.getSubtaskById(subtask1.getId()).getName());
    }

    @Test
    void shouldGetAllTasks() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
        Task task2 = taskManager.addTask(new Task("Task", "Description2"));
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Ожидается 2 задачи в списке");
        assertTrue(tasks.contains(task1), "Список должен содержать первую задачу");
        assertTrue(tasks.contains(task2), "Список должен содержать вторую задачу");
    }

    @Test
    void shouldGetAllEpics() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        Epic epic2 = taskManager.addEpic(new Epic("Epic", "Description2"));
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, epics.size(), "Ожидается 2 эпика в списке");
        assertTrue(epics.contains(epic1), "Список должен содержать первый эпик");
        assertTrue(epics.contains(epic2), "Список должен содержать второй эпик");
    }

    @Test
    void shouldGetAllSubtasks() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(2, subtasks.size(), "Ожидается 2 подзадачи в списке");
        assertTrue(subtasks.contains(subtask1), "Список должен содержать первую подзадачу");
        assertTrue(subtasks.contains(subtask2), "Список должен содержать вторую подзадачу");
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
        Task task2 = taskManager.addTask(new Task("Task", "Description2"));

        taskManager.removeTask(task1.getId());

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "После удаления 1 задачи должно остаться 1 задача");
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        Epic epic2 = taskManager.addEpic(new Epic("Epic", "Description2"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        taskManager.removeEpic(epic1.getId());

        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasksAfterRemove = taskManager.getAllSubtasks();
        assertEquals(1, epics.size(), "После удаления 1 эпика должен остаться 1 эпик");
        assertEquals(0, subtasksAfterRemove.size(), "Подзадачи удаленного эпика тоже должны удалиться");
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        taskManager.removeSubtask(subtask1.getId());
        taskManager.removeSubtask(subtask2.getId());

        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(0, subtasks.size(), "После удаления 2 подзадач должно остатсься 0 подзадач");
    }

    @Test
    void shouldClearAllTasks() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));

        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldClearAllEpics() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));

        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void shouldClearAllSubtasks() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldUpdateTaskCorrectly() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
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
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        Epic updated = new Epic("Updated", "Updated Description");
        updated.setId(epic1.getId());

        taskManager.updateEpic(updated);

        Epic result = taskManager.getEpicById(epic1.getId());

        assertEquals("Updated", result.getName(), "Имя должно совпадать");
        assertEquals("Updated Description", result.getDescription(), "Описание должно совпадать");
    }

    @Test
    void shouldUpdateSubtaskCorrectly() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
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
    void shouldRemoveNotActualSubtasks() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));

        taskManager.removeSubtask(subtaskId1);

        Epic updatedEpic = taskManager.getEpicById(epic1.getId());

        assertFalse(updatedEpic.getSubtaskIds().contains(subtaskId1));
    }

    @Test
    void shouldRemoveEpicAndAllItsSubtasks() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));

        taskManager.removeEpic(epic1.getId());

        assertThrows(NotFoundException.class, () -> taskManager.getEpicById(epic1.getId()), "Эпик должен быть удален");
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtaskId1), "1 Подзадача эпика должна быть удалена");
        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtaskId2), "2 Подзадача эпика должна быть удалена");
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
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
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

    @Test
    void shouldSubtaskConnectedWithEpic() {
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        assertEquals(epic1.getId(), subtask1.getEpicId(), "Id эпика должно совпадать");
    }

    @Test
    void shouldReturnHistoryFromManager() {
        Task task1 = taskManager.addTask(new Task("Task1", "Description1"));
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task1, history.get(0), "В истории должна быть именно эта задача");
    }

    @Test
    void shouldPrioritizeTasksWithNoTimeConflicts() {
        Task task1 = taskManager.addTask(new Task("Task", "Description1"));
        Epic epic1 = taskManager.addEpic(new Epic("Epic", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Subtask", "Subtask", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        task1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.updateTask(task1);

        subtask1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 30));
        subtask1.setDuration(Duration.ofMinutes(20));
        taskManager.updateSubtask(subtask1);

        subtask2.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 50));
        subtask2.setDuration(Duration.ofMinutes(20));
        taskManager.updateSubtask(subtask2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritized.size(), "Должно быть добавлено три задачи");
        assertTrue(prioritized.get(0).getStartTime().isBefore(prioritized.get(1).getStartTime()));
        assertTrue(prioritized.get(1).getStartTime().isBefore(prioritized.get(2).getStartTime()));
    }

    @Test
    void shouldThrowExceptionWhenTasksOverlapInTime() {
        Task task1 = new Task("Task1", "Description1");
        task1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 0));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("Task2", "Description2");
        task2.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 10));
        task2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class,
                () -> taskManager.addTask(task2),
                "Должно выбрасываться исключение при пересечении времени");
    }
}