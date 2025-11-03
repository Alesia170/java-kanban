package ru.practicum.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.manager.TaskManager;
import ru.practicum.server.Endpoint;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (endpoint.equals(Endpoint.GET)) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange, "Неизвестный тип запроса");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] partsPath = requestPath.split("/");

        if ("GET".equals(requestMethod) && partsPath.length >= 2) {
            return Endpoint.GET;
        }

        return Endpoint.UNKNOWN;
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response);

    }
}
