package ru.practicum.test.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.practicum.manager.InMemoryHistoryManager;
import ru.practicum.manager.InMemoryTaskManager;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.HttpTaskServer;

import java.io.IOException;

public abstract class HttpBaseTest {
    protected TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    protected HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    protected Gson gson = HttpTaskServer.getGson();

    protected HttpBaseTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }
}
