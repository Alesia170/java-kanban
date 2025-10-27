package ru.practicum.manager;

import ru.practicum.exception.ManagerSaveException;
import ru.practicum.tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

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
        task1.setStartTime(LocalDateTime.of(2025, 10, 19, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.updateTask(task1);

        task2.setStatus(Status.DONE);
        manager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.of(2025, 10, 19, 10, 30));
        subtask1.setDuration(Duration.ofMinutes(30));
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setStartTime(LocalDateTime.of(2025, 10, 19, 11, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        manager.updateSubtask(subtask2);

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
            int maxId = 0;

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = TaskConverter.fromString(line);

                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                    case SUBTASK -> manager.subtasks.put(task.getId(), (Subtask) task);
                }

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }

            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtaskIds().add(subtask.getId());
                }
            }

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicTimeAndDuration(epic.getId());
                manager.updateEpicStatus(epic.getId());
            }

            manager.updateId(maxId);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        } catch (RuntimeException e) {
            throw new ManagerSaveException("Ошибка при чтении файла: некорректный формат данных", e);
        }
        return manager;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(TaskConverter.taskToString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(TaskConverter.taskToString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(TaskConverter.taskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        } catch (RuntimeException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: некорректный формат данных", e);
        }
    }
}