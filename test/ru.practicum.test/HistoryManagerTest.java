package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.tasks.Status.*;

public class HistoryManagerTest extends BaseClass {

    @Test
    void historyKeepPreviousVersionTasks() {
        Task task = new Task("Test Task", "Test description");
        Task savedTask = taskManager.addTask(task);

        historyManager.add(savedTask);

        task.setName("Test new");
        task.setDescription("Test new");
        task.setStatus(IN_PROGRESS);
        taskManager.updateTask(savedTask);

        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty());
        Task fromHistory = history.get(history.size() - 1);

        assertEquals("Test Task", fromHistory.getName(), "История должна хранить старую задачу");
        assertEquals("Test description", fromHistory.getDescription(), "История должна хранить " +
                "старое описание");
        assertEquals(NEW, fromHistory.getStatus(), "История должна хранить старый статус");
        assertEquals(task.getId(), fromHistory.getId(), "ID совпадает, но данные — из прошлого среза");
    }
}
