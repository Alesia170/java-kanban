package ru.practicum.test.server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest extends HttpBaseTest {

    public HttpTaskManagerEpicsTest() throws IOException {

    }

    @Test
    void shouldEpicAdd() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Name1", "Description1");

        String epicJson = gson.toJson(epic1);

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Name1", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        Epic epic2 = taskManager.addEpic(new Epic("Name2", "Description2"));
        Epic epic3 = taskManager.addEpic(new Epic("Name3", "Description3"));

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsFromServer = gson.fromJson(response.body(), listType);

        assertEquals(200, response.statusCode());
        assertNotNull(epicsFromServer, "Список эпиков не должен быть null");
        assertEquals(3, epicsFromServer.size(), "Количество эпиков некорректно");
        assertEquals("Name1", epicsFromServer.get(0).getName());
        assertEquals("Name2", epicsFromServer.get(1).getName());
        assertEquals("Name3", epicsFromServer.get(2).getName());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int epicId = epic1.getId();

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Epic epicFromServer = gson.fromJson(getResponse.body(), Epic.class);

        assertEquals(200, getResponse.statusCode());
        assertNotNull(epicFromServer, "Эпик не должен быть null");
        assertEquals(epicId, epicFromServer.getId(), "ID должен совпадать");
        assertEquals("Name1", epicFromServer.getName(), "Имя эпика некорректно");
    }

    @Test
    void shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Name2", "Description1", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        int epicId = epic1.getId();

        HttpClient httpClient = HttpClient.newHttpClient();
        URI getUrl = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        Type subtaskListType = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Subtask> subtasks = gson.fromJson(getResponse.body(), subtaskListType);

        assertEquals(200, getResponse.statusCode());
        assertNotNull(subtasks, "Список подзадач не должен быть null");
        assertEquals(2, subtasks.size(), "Должно быть две подзадачи");
    }

    @Test
    void shouldEpicDeleteById() throws IOException, InterruptedException {
        Epic epic1 = taskManager.addEpic(new Epic("Name1", "Description1"));

        int id = epic1.getId();

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
    }

    @Test
    void shouldReturn404WhenEpicIdDoesNotExist() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        int id = 100;
        URI url = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}