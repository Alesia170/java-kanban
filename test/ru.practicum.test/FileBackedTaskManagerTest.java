package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.*;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;
import ru.practicum.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        File file = File.createTempFile("test", "csv");

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty(), "Не должно быть задач");
        assertTrue(loaded.getAllEpics().isEmpty(), "Не должно быть эпиков");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Не должно быть подзадач");
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() throws IOException {
        File file = File.createTempFile("test", "csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task1 = manager.addTask(new Task("Name1", "Description1"));
        Epic epic1 = manager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = manager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = manager.getSubtaskById(subtaskId1);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = manager.getAllTasks();
        List<Epic> epics = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtasks();

        List<Task> loadedTasks = loaded.getAllTasks();
        List<Epic> loadedEpics = loaded.getAllEpics();
        List<Subtask> loadedSubtasks = loaded.getAllSubtasks();

        assertEquals(tasks.size(), loaded.getAllTasks().size(), "Количество задач должно совпадать");
        assertEquals(epics.size(), loaded.getAllEpics().size(), "Количество эпиков должно совпадать");
        assertEquals(subtasks.size(), loaded.getAllTasks().size(), "Количество подзадач должно совпадать");

        assertEquals(tasks.get(0).getName(), loadedTasks.get(0).getName(), "Название задачи должно совпадать");
        assertEquals(epics.get(0).getName(), loadedEpics.get(0).getName(), "Название эпиков должно совпадать");
        assertEquals(subtasks.get(0).getName(), loadedSubtasks.get(0).getName(), "Название подзадач должно совпадать");
    }
}
