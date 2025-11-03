package ru.practicum.test.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

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
import static ru.practicum.tasks.Status.IN_PROGRESS;

public class HttpTaskManagerTasksTest extends HttpBaseTest {

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Name1", "Description1");
        task1.setStartTime(LocalDateTime.of(2025, 11, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Name1", "Description1");
        task2.setStartTime(LocalDateTime.of(2025, 11, 1, 10, 0));
        task2.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(406, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Name1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("Name1", "Description1"));

        String taskJson = gson.toJson(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task updated = gson.fromJson(response.body(), Task.class);
        updated.setStatus(IN_PROGRESS);
        updated.setName("ChangingName");

        String updatedJson = gson.toJson(updated);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("ChangingName", tasksFromManager.get(0).getName(), "После обновления задачи имя должно измениться");
        assertEquals(IN_PROGRESS, tasksFromManager.get(0).getStatus());
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Name1", "Description1");
        Task task2 = new Task("Name2", "Description2");

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();

        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response3 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response3.statusCode());

        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromServer = gson.fromJson(response3.body(), listType);

        assertNotNull(tasksFromServer, "Список задач не должен быть null");
        assertEquals(2, tasksFromServer.size(), "Количество задач некорректно");
        assertEquals("Name1", tasksFromServer.get(0).getName());
        assertEquals("Name2", tasksFromServer.get(1).getName());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("Name1", "Description1"));
        String taskJson1 = gson.toJson(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task createdTask = gson.fromJson(response.body(), Task.class);
        int taskId = createdTask.getId();
        URI getUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> response2 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }

    @Test
    void shouldReturn404WhenTaskIdDoesNotExist() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        int id = 100;
        URI url = URI.create("http://localhost:8080/tasks/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = taskManager.addTask(new Task("Name1", "Description1"));
        String taskJson1 = gson.toJson(task1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task createdTask = gson.fromJson(response.body(), Task.class);
        int taskId = createdTask.getId();
        URI getUrl = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .DELETE()
                .build();

        HttpResponse<String> response2 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }
}
