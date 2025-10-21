package ru.practicum.manager;

import ru.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {

    public static String taskToString(Task task) {

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "",
                task.getStartTime() != null ? task.getStartTime().toString() : "",
                String.valueOf(task.getEpicIdForFile())
        );
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",", 8);

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = !fields[5].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[5])) : null;
        LocalDateTime startTime = !fields[6].isEmpty() ? LocalDateTime.parse(fields[6]) : null;
        String epicField = fields[7];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;

            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
