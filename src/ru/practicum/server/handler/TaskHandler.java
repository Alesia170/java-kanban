package ru.practicum.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.Endpoint;
import ru.practicum.tasks.Task;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET:
                    handleGetTasks(exchange);
                    break;
                case GET_BY_ID:
                    handleGetTaskById(exchange);
                    break;
                case POST:
                    handlePostTask(exchange);
                    break;
                case DELETE:
                    handleDeleteTask(exchange);
                    break;
                case UNKNOWN:
                    break;
                default:
                    sendNotFound(exchange, "Неизвестный тип запроса");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, "Внутренняя ошибка: " + e.getMessage());
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length < 2 || !pathParts[1].equals("tasks")) {
            return Endpoint.UNKNOWN;
        }

        if ("GET".equals(requestMethod)) {
            if (pathParts.length == 2) {
                return Endpoint.GET;
            } else if (pathParts.length == 3) {
                return Endpoint.GET_BY_ID;
            }
        }

        if ("POST".equals(requestMethod)) return Endpoint.POST;

        if ("DELETE".equals(requestMethod)) return Endpoint.DELETE;

        return Endpoint.UNKNOWN;
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());

        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getIdFromPath(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Задача отсуствует");
            return;
        }

        int id = taskIdOpt.get();

        try {
            Task task = taskManager.getTaskById(id);
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Задача с id=" + id + " не найдена");

        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() == 0) {
                taskManager.addTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                taskManager.updateTask(task);
                sendCreated(exchange, gson.toJson(task));
            }
        } catch (IllegalArgumentException exception) {
            sendHasInteractions(exchange, exception.getMessage());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, exception.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getIdFromPath(exchange);

        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Задача отсутсвует");
            return;
        }

        int id = taskIdOpt.get();

        try {
            taskManager.removeTask(id);
            sendText(exchange, "Задача успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Задача с id= " + id + " не найдена");
        }
    }
}
