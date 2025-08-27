package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.*;
import ru.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Managers.getDefault() вернул null");
        assertNotNull(taskManager.getAllTasks(), "Задачи не инициализованы");
        assertNotNull(taskManager.getAllEpics(), "Эпики не инициализованы");
        assertNotNull(taskManager.getAllSubtasks(), "Подзадачи не инициализованы");
    }

    @Test
    void getDefaultHistoryManagerIsInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "История не должна быть null");

        Task task1 = new Task("Test: addNewTask", "Description: Task");
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}