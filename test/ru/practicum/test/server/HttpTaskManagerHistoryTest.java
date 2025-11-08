package ru.practicum.test.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerHistoryTest extends HttpBaseTest{

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("Task1", "Description1"));
        Task task2 = taskManager.addTask(new Task("Task2", "Description2"));
        Epic epic1 = taskManager.addEpic(new Epic("Epic1", "Description1"));

        String taskJson = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        String epicJson = gson.toJson(epic1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI taskUrl = URI.create("http://localhost:8080/tasks");
        URI epicUrl = URI.create("http://localhost:8080/epics");

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

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(epicUrl)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());

        Task createdTask1 = gson.fromJson(response.body(), Task.class);
        Task createdTask2 = gson.fromJson(response2.body(), Task.class);
        Epic createdEpic = gson.fromJson(response3.body(), Epic.class);

        HttpRequest getTask1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + createdTask1.getId()))
                .GET()
                .build();
        httpClient.send(getTask1, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTask2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + createdTask2.getId()))
                .GET()
                .build();
        httpClient.send(getTask2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + createdEpic.getId()))
                .GET()
                .build();
        httpClient.send(getEpic, HttpResponse.BodyHandlers.ofString());

        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> historyResponse = httpClient.send(historyRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, historyResponse.statusCode(), "История должна успешно вернуться");

        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(historyResponse.body(), listType);

        assertNotNull(history, "История не должна быть null");
        assertEquals(3, history.size(), "История должна содержать 3 элемента");

        assertEquals(createdTask1.getId(), history.get(0).getId(), "Первый в истории task1");
        assertEquals(createdTask2.getId(), history.get(1).getId(), "Второй в истории task2");
        assertEquals(createdEpic.getId(), history.get(2).getId(), "Третий в истории epic1");
    }
}
