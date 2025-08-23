package ru.practicum.test;

import org.junit.jupiter.api.Test;
import ru.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SubtaskTest extends BaseClass {

    @Test
    void subtaskCannotMakeItselfAsEpic() {

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Epic savedEpic = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask", epic.getId());
        Subtask savedSubtask = taskManager.addSubtask(subtask);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(savedEpic.getId(), savedSubtask.getEpicId());
        assertTrue(savedSubtask.getId() != savedSubtask.getEpicId());
        assertFalse(savedEpic.getSubtaskIds().contains(savedEpic.getId()));
    }
}
