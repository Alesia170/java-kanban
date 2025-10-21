package ru.practicum.tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String getEpicIdForFile() {
        return String.valueOf(epicId);
    }

    @Override
    public void setId(int id) {
        if (id == this.epicId) {
            return;
        }
        super.setId(id);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + getEpicId() +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "min" : "null") +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
