package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.exception.ManagerSaveException;
import ru.practicum.manager.*;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;
import ru.practicum.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test", "csv");
        taskManager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty(), "Не должно быть задач");
        assertTrue(loaded.getAllEpics().isEmpty(), "Не должно быть эпиков");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Не должно быть подзадач");
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        Task task1 = taskManager.addTask(new Task("Name1", "Description1"));
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        List<Task> tasks = taskManager.getAllTasks();
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubtasks();

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

    @Test
    void shouldSaveAndLoadTasksWithDurationAndStartTime() {
        Task task1 = taskManager.addTask(new Task("Name1", "Description1"));
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 0));

        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));

        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setDuration(Duration.ofMinutes(20));
        subtask1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 0));
        taskManager.updateSubtask(subtask1);

        int subtaskId2 = taskManager.addSubtask(new Subtask("Name2", "Description2", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);
        subtask2.setDuration(Duration.ofMinutes(10));
        subtask2.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 20));
        taskManager.updateSubtask(subtask2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task loadedTask = loaded.getTaskById(task1.getId());
        Epic loadedEpic = loaded.getEpicById(epic1.getId());
        Subtask loadedSubtask1 = loaded.getSubtaskById(subtaskId1);
        Subtask loadedSubtask2 = loaded.getSubtaskById(subtaskId2);

        assertEquals(Duration.ofMinutes(30), loadedTask.getDuration());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 0), loadedTask.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 30), loadedTask.getEndTime());

        assertEquals(Duration.ofMinutes(20), loadedSubtask1.getDuration());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 0), loadedSubtask1.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 20), loadedSubtask1.getEndTime());

        assertEquals(Duration.ofMinutes(10), loadedSubtask2.getDuration());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 20), loadedSubtask2.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 30), loadedSubtask2.getEndTime());

        assertEquals(Duration.ofMinutes(30), loadedEpic.getDuration());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 0), loadedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 19, 14, 30), loadedEpic.getEndTime());
    }

    @Test
    void shouldThrowExceptionWhenFileIsBroken() throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            writer.write("line,columns\n");
        }

        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(file),
                "Должен выбрасывать ManagerSaveException при ошибке чтения повреждённого файла");
    }

    @Test
    void shouldThrowExceptionWhenLoadFromFile() {
        File invalidFile = new File("no/folder/test.csv");

        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(invalidFile),
                "Должен выбрасывать ManagerSaveException при попытке загрузить недоступный файл");
    }

    @Test
    void shouldNotThrowExceptionWhenSavingAndLoadingFile() {
        Task task1 = new Task("Name1", "Description1");

        assertDoesNotThrow(() -> taskManager.addTask(task1),
                "Не должно выбрасываться исключение при сохранении задачи");

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file),
                "Не должно выбрасываться исключение при чтении файла");
    }
}
