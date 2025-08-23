package ru.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import ru.practicum.manager.*;

abstract class BaseClass {
    protected TaskManager taskManager;
    protected HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }
}
