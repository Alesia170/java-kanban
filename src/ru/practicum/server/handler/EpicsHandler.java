package ru.practicum.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.exception.NotFoundException;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.Endpoint;
import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET:
                handleGetEpics(exchange);
                break;
            case GET_BY_ID:
                handleGetEpicById(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case POST:
                handlePostEpic(exchange);
                break;
            case DELETE:
                handleDeleteEpic(exchange);
                break;
            case UNKNOWN:
                sendNotFound(exchange, "Неизвестный эндпоинт");
                break;
            default:
                sendNotFound(exchange, "Неизвестный тип запроса");
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length < 2 || !pathParts[1].equals("epics")) {
            return Endpoint.UNKNOWN;
        }

        if ("GET".equals(requestMethod)) {
            if (pathParts.length == 2) {
                return Endpoint.GET;
            } else if (pathParts.length == 3) {
                return Endpoint.GET_BY_ID;
            } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
        }

        if ("POST".equals(requestMethod)) return Endpoint.POST;

        if ("DELETE".equals(requestMethod)) return Endpoint.DELETE;

        return Endpoint.UNKNOWN;
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());

        sendText(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getIdFromPath(exchange);

        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange, "Эпик отсутствует");
            return;
        }

        int id = epicIdOpt.get();

        try {
            Epic epic = taskManager.getEpicById(id);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getIdFromPath(exchange);

        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange, "Эпик отсутствует");
            return;
        }

        int id = epicIdOpt.get();

        try {
            List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.addEpic(epic);

            sendCreated(exchange, gson.toJson(epic));

        } catch (IllegalArgumentException exception) {
            sendHasInteractions(exchange, exception.getMessage());
        } catch (NotFoundException exception) {
            sendNotFound(exchange, exception.getMessage());
        } catch (Exception e) {
            sendInternalServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getIdFromPath(exchange);

        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange, "Эпик отсутсвует");
            return;
        }

        try {
            int id = epicIdOpt.get();
            taskManager.removeEpic(id);
            sendText(exchange, "Эпик успешно удален");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
