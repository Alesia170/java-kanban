package ru.practicum.manager;

import ru.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator
                    .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );
    private int nextId = 1;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task addTask(Task task) {
        if (hasOverlaps(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }

        Task copyTask = new Task(task.getName(), task.getDescription());
        copyTask.setId(generateId());
        copyTask.setStatus(task.getStatus());
        copyTask.setDuration(task.getDuration());
        copyTask.setStartTime(task.getStartTime());
        tasks.put(copyTask.getId(), copyTask);
        addToPrioritized(copyTask);
        return copyTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }

        if (hasOverlaps(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }

        tasks.put(task.getId(), task);
        addToPrioritized(task);
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic epicCopy = new Epic(epic.getName(), epic.getDescription());
        epicCopy.setId(generateId());
        epics.put(epicCopy.getId(), epicCopy);
        return epicCopy;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        epics.put(epicId, epic);
        updateEpicStatus(epicId);
        updateEpicTimeAndDuration(epicId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic == null) return;
        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(subtaskId);
        }
        historyManager.remove(epicId);
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        epics.keySet().forEach(historyManager::remove);

        epics.clear();
        subtasks.clear();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпик с id = " + epicId + " не найден. Подзадача не добавлена.");
            return 0;
        }

        if (hasOverlaps(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой подзадачей.");
        }

        Subtask subtaskCopy = new Subtask(subtask.getName(), subtask.getDescription(), epicId);
        int id = generateId();
        subtaskCopy.setId(id);
        subtaskCopy.setStatus(subtask.getStatus());
        subtaskCopy.setDuration(subtask.getDuration());
        subtaskCopy.setStartTime(subtask.getStartTime());
        subtasks.put(id, subtaskCopy);
        epic.addSubtaskId(id);
        addToPrioritized(subtaskCopy);
        updateEpicStatus(epicId);
        updateEpicTimeAndDuration(epicId);
        return id;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) return;

        if (hasOverlaps(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой подзадачей.");
        }

        subtasks.put(subtask.getId(), subtask);
        addToPrioritized(subtask);
        updateEpicStatus(subtask.getEpicId());
        updateEpicTimeAndDuration(subtask.getEpicId());
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            return;
        }
        prioritizedTasks.remove(subtask);
        historyManager.remove(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(subtaskId);
            updateEpicStatus(epic.getId());
            updateEpicTimeAndDuration(epic.getId());
        }
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicTimeAndDuration(epic.getId());
        });
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask : epicSubtasks) {
            Status status = subtask.getStatus();
            if (status != Status.DONE) {
                allDone = false;
            }
            if (status != Status.NEW) {
                allNew = false;
            }
            if (!allDone && !allNew) {
                break;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTimeAndDuration(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasks = getEpicSubtasks(epicId);

        if (subtasks.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime earliestStart = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(totalDuration);
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
    }

    private void addToPrioritized(Task task) {
        prioritizedTasks.remove(task);
        prioritizedTasks.add(task);
    }

    private boolean isOverLapping(Task task1, Task task2) {
        if (task1.getId() == task2.getId()) {
            return false;
        }

        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean hasOverlaps(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(task -> isOverLapping(newTask, task));
    }

    private int generateId() {
        return nextId++;
    }
}
