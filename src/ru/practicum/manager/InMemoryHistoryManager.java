package ru.practicum.manager;

import ru.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_LIMIT = 10;
    private final List<Task> history = new ArrayList<>(HISTORY_LIMIT);

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public Task add(Task task) {
        if (task != null) {
            history.add(task);
            checkLimitHistory();
        }
        return task;
    }

    private void checkLimitHistory() {
        if (history.size() > HISTORY_LIMIT) {
            history.remove(0);
        }
    }
}
