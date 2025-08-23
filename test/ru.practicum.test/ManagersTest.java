package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest extends BaseClass{

    @Test
    void getDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Managers.getDefault() вернул null");
        assertNotNull(taskManager.getAllTasks(), "Заачи не инициализованы");
        assertNotNull(taskManager.getAllEpics(), "Эпики не инициализованы");
        assertNotNull(taskManager.getAllSubtasks(), "Подзадачи не инициализованы");
    }

    @Test
    void getDefaultHistoryManagerIsInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Test: addNewTask", "Description: Task");
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(historyManager, "История не должна быть null");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }
}