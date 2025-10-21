package ru.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        if (id == this.getId()) {
            return;
        }
        subtaskIds.add(id);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "min" : "null") +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
