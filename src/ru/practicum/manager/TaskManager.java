package ru.practicum.manager;

import ru.practicum.tasks.Epic;
import ru.practicum.tasks.Subtask;
import ru.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Task getTaskById(int id);

    ArrayList<Task> getAllTasks();

    void updateTask(Task task);

    void removeTask(int id);

    void removeAllTasks();

    Epic addEpic(Epic epic);

    Epic getEpicById(int id);

    ArrayList<Epic> getAllEpics();

    void updateEpic(Epic epic);

    void removeEpic(int epicId);

    void removeAllEpics();

    Subtask addSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    ArrayList<Subtask> getAllSubtasks();

    void updateSubtask(Subtask subtask);

    void removeSubtask(int subtaskId);

    void removeAllSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
