package ru.practicum.test;

import ru.practicum.tasks.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest extends BaseClass {

    @Test
    void epicCannotContainItselfIfAsSubtask() {

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Epic savedEpic = taskManager.addEpic(epic);

        Subtask subtaskFake = new Subtask("Test addNewSubtaskFake", "Test addNewSubtaskFake",
                epic.getId());
        Subtask savedSubtaskFake = taskManager.addSubtask(subtaskFake);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertTrue(savedSubtaskFake.getId() != savedEpic.getId(), "ID подзадачи не должен " +
                "совпадать с ID эпика");
        assertTrue(savedEpic.getSubtaskIds().contains(savedSubtaskFake.getId()), "Подзадача должна " +
                "числиться у эпика");
        assertFalse(savedEpic.getSubtaskIds().contains(savedEpic.getId()), "Эпик не должен содержать свой " +
                "собственный id среди подзадач");
    }
}