package ru.practicum.manager;

import ru.practicum.tasks.*;

public class TaskConverter {

    public static String taskToString(Task task) {

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                String.valueOf(task.getEpicIdForFile())
        );
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",", 6);

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicField = fields[5];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                return epic;

            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
