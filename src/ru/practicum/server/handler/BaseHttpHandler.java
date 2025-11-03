package ru.practicum.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, 200, text);

    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        sendResponse(h, 201, text);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendResponse(h, 404, text);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendResponse(h, 406, text);
    }

    protected void sendInternalServerError(HttpExchange h, String text) throws IOException {
        sendResponse(h, 500, text);
    }

    private void sendResponse(HttpExchange h, int statusCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
