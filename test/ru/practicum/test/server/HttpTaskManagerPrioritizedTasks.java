package ru.practicum.test.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTasks extends HttpBaseTest {

    public HttpTaskManagerPrioritizedTasks() throws IOException {
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("Task1", "Description1"));
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 0));

        Task task2 = taskManager.addTask(new Task("Task2", "Description2"));
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2025, 10, 19, 14, 30));

        String taskJson = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI taskUrl = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(taskUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(taskUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        Task createdTask1 = gson.fromJson(response.body(), Task.class);
        Task createdTask2 = gson.fromJson(response2.body(), Task.class);

        HttpRequest prioritizedRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> prioritizedResponse = httpClient.send(prioritizedRequest,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, prioritizedResponse.statusCode());

        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritized = gson.fromJson(prioritizedResponse.body(), listType);

        assertNotNull(prioritized, "Список приоритетных задач не должен быть null");
        assertEquals(2, prioritized.size(), "Должно быть две задачи");
        assertEquals(createdTask1.getId(), prioritized.get(0).getId(), "Первой задачей должна быть task1");
        assertEquals(createdTask2.getId(), prioritized.get(1).getId(), "Второй задачей должно быть task2");
    }
}
