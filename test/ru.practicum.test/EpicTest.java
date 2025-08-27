package ru.practicum.test;

import ru.practicum.tasks.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicShouldBeEqualIfIdsSame() {
        Epic epic1 = new Epic("Epic1", "Description1");
        epic1.setId(10);
        Epic epic2 = new Epic("Epic2", "Description2");
        epic2.setId(10);

        assertEquals(epic1, epic2, "Задачи с одинаковым ID должны быть равны");
        assertEquals(epic1.hashCode(), epic2.hashCode(), "HashCode с одинаковым ID должны быть равны");
    }

    @Test
    void epicCannotContainItselfIfAsSubtask() {
        int subtaskId = 15;
        Epic epic1 = new Epic("Epic1", "Description1");
        epic1.setId(10);
        epic1.addSubtaskId(subtaskId);
        epic1.addSubtaskId(epic1.getId());

        List<Integer> subtaskIds = epic1.getSubtaskIds();

        assertTrue(subtaskIds.contains(subtaskId));
        assertFalse(subtaskIds.contains(epic1.getId()), "Эпик не должен содержать сам себя как подзадачу");
    }
}