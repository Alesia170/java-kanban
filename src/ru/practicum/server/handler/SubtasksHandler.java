package ru.practicum.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.Endpoint;
import ru.practicum.tasks.Subtask;

import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET:
                handleGetSubtasks(exchange);
                break;
            case GET_BY_ID:
                handleGetSubtaskById(exchange);
                break;
            case POST:
                handlePostSubtask(exchange);
                break;
            case DELETE:
                handleDeleteSubtask(exchange);
                break;
            case UNKNOWN:
                break;
            default:
                sendNotFound(exchange, "Неизвестный тип запроса");
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length < 2 || !pathParts[1].equals("subtasks")) {
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

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());

        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getIdFromPath(exchange);

        if (subtaskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Подзадача отсуствует");
            return;
        }

        int id = subtaskIdOpt.get();

        try {
            Subtask subtask = taskManager.getSubtaskById(id);
            String response = gson.toJson(subtask);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }

            sendCreated(exchange, gson.toJson(subtask));

        } catch (IllegalArgumentException exception) {
            sendHasInteractions(exchange, exception.getMessage());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, exception.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getIdFromPath(exchange);

        if (subtaskIdOpt.isEmpty()) {
            sendNotFound(exchange, "Подзадача отсутсвует");
            return;
        }

        int id = subtaskIdOpt.get();

        try {
            taskManager.removeSubtask(id);
            sendText(exchange, "Подзадача успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
        }
    }
}
