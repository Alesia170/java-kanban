package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.*;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;
import ru.practicum.tasks.Task;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException{
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
        Task task2 = manager.addTask(new Task("Name2", "Description2"));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loaded.getAllTasks().size(), "Должно быть 2 задачи");
        Task task3 = loaded.addTask(new Task("Name3", "Description3"));

        assertNotEquals(task1.getId(), task3.getId(), "ID новой задачи не должен совпадать со старой");
    }

    @Test
    void shouldSaveAndLoadEpicsAndSubtasksCorrectly() throws IOException {
        File file = File.createTempFile("test", "csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Epic epic1 = manager.addEpic(new Epic("Name1", "Description1"));
        Epic epic2 = manager.addEpic(new Epic("Name2", "Description2"));

        int subtaskId1 = manager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = manager.getSubtaskById(subtaskId1);
        int subtaskId2 = manager.addSubtask(new Subtask("Name2", "Description2", epic1.getId()));
        Subtask subtask2 = manager.getSubtaskById(subtaskId2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loaded.getAllEpics().size(), "Должно быть 2 эпика");
        assertEquals(2, loaded.getAllSubtasks().size(), "Должно быть 2 подзадачи");

        Epic epic3 = loaded.addEpic(new Epic("Name3", "Description3"));
        int subtaskId3 = loaded.addSubtask(new Subtask("Name3", "Description3", epic3.getId()));
        Subtask subtask3 = loaded.getSubtaskById(subtaskId3);

        Subtask loadedSubtask1 = loaded.getSubtaskById(subtask1.getId());
        assertEquals(epic1.getId(), loadedSubtask1.getEpicId(), "EpicId у подзадачи должен сохраниться");

        assertNotEquals(epic2.getId(), epic3.getId(), "ID новой задачи не должен совпадать со старой");
        assertNotEquals(subtask1.getId(), subtask3.getId(), "ID новой подзадачи не должен совпадать со старой");
    }
}
