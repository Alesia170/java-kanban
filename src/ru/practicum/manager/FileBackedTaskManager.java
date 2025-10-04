package ru.practicum.manager;

import ru.practicum.exception.ManagerSaveException;
import ru.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task1 = manager.addTask(new Task("Name1", "Description1"));
        Task task2 = manager.addTask(new Task("Name2", "Description2"));

        Epic epic1 = manager.addEpic(new Epic("Name1", "Description1"));
        Epic epic2 = manager.addEpic(new Epic("Name2", "Description2"));

        int subtaskId1 = manager.addSubtask(new Subtask("Name1", "Description1", epic1.getId()));
        Subtask subtask1 = manager.getSubtaskById(subtaskId1);
        int subtaskId2 = manager.addSubtask(new Subtask("Name2", "Description2", epic1.getId()));
        Subtask subtask2 = manager.getSubtaskById(subtaskId2);


        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        task2.setStatus(Status.DONE);
        manager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        System.out.println("До загрузки: " + manager.getAllTasks() + "\n" +
                manager.getAllEpics() + "\n" + manager.getAllSubtasks());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        System.out.println("После загрузки: " + loaded.getAllTasks() + "\n" +
                loaded.getAllEpics() + "\n" + loaded.getAllSubtasks());

        Task task3 = loaded.addTask(new Task("Name3", "Description3"));
        Epic epic3 = loaded.addEpic(new Epic("Name3", "Description3"));
        int subtaskId3 = loaded.addSubtask(new Subtask("Name3", "Description3", epic3.getId()));
        Subtask subtask3 = loaded.getSubtaskById(subtaskId3);

        System.out.println("После добавления новых задач: " + loaded.getAllTasks() + "\n" +
                loaded.getAllEpics() + "\n" + loaded.getAllSubtasks());
    }

    @Override
    public Task addTask(Task task) {
        Task saved = super.addTask(task);
        save();
        return saved;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic saved = super.addEpic(epic);
        save();
        return saved;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int saved = super.addSubtask(subtask);
        save();
        return saved;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = manager.fromString(line);

                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        return manager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    private String taskToString(Task task) {
        TaskType type = TaskType.TASK;
        String epicId = "";

        if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else if (task instanceof Subtask sub) {
            type = TaskType.SUBTASK;
            epicId = String.valueOf(sub.getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type.name(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                epicId
        );
    }

    private Task fromString(String value) {
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