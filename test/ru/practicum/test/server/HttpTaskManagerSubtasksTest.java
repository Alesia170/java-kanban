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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest extends HttpBaseTest {

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @Test
    void shouldSubtaskAdd() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        String subtaskJson = gson.toJson(subtask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Name1", subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void shouldSubtaskUpdate() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        subtask1.setName("ChangingName");
        taskManager.updateSubtask(subtask1);

        String subtaskJson = gson.toJson(subtask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("ChangingName", subtasksFromManager.get(0).getName(), "После обновления подзадачи имя должно измениться");
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Name2", "Description1", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        String subtaskJson1 = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .build();

        HttpResponse<String> response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response3 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response3.statusCode());

        Type listType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksFromServer = gson.fromJson(response3.body(), listType);

        assertNotNull(subtasksFromServer, "Список подзадач не должен быть null");
        assertEquals(2, subtasksFromServer.size(), "Количество подзадач некорректно");
        assertEquals("Name1", subtasksFromServer.get(0).getName());
        assertEquals("Name2", subtasksFromServer.get(1).getName());
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);

        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask createdSubtask = gson.fromJson(response.body(), Subtask.class);
        int subtaskId = createdSubtask.getId();
        URI getUrl = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> response2 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }

    @Test
    void shouldReturn404WhenSubtaskIdDoesNotExist() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        int id = 100;
        URI url = URI.create("http://localhost:8080/subtasks/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        String subtaskJson1 = gson.toJson(subtask1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask createdSubtask = gson.fromJson(response.body(), Subtask.class);
        int subtaskId = createdSubtask.getId();
        URI getUrl = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .DELETE()
                .build();

        HttpResponse<String> response2 = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());
    }
}
