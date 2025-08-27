package ru.practicum.manager;

import ru.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {
    Task add(Task task);

    List<Task> getHistory();
}
