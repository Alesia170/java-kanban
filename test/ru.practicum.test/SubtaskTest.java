package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void subtasksShouldBeEqualsWithTheSameIds() {
        int epicId = 10;
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epicId);

        subtask1.setId(42);
        subtask2.setId(42);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым ID должны быть равны");
        assertEquals(subtask1.hashCode(), subtask2.hashCode(), "HashCode с одинаковым ID должен совпадать");
    }

    @Test
    void subtaskCannotMakeItselfAsEpic() {
        int epicId = 10;
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epicId);

        subtask1.setId(epicId);

        assertNotEquals(subtask1.getId(), subtask1.getEpicId(), "Subtask нельзя сделать своим же Epic");
    }

    @Test
    void epicsAndSubtasksWithTheSameIdsNotEqual() {
        Task epic1 = new Epic("Epic1", "Description1");
        Task subtask1 = new Subtask("Subtask", "Description", epic1.getId());

        epic1.setId(26);
        subtask1.setId(26);

        assertNotEquals(epic1, subtask1, "Эпик и подзадача с одинаковым ID не должны быть равны");
    }
}
