package ru.practicum;

import ru.practicum.manager.HistoryManager;
import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Купить продукты", "Сделать покупки в магазине");
        taskManager.addTask(task1);
        Task task2 = new Task("Вымыть машину", "Помыть салон");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Переезд", "Организовать переезд в новую квартиру");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Упаковать вещи", "Собрать коробки", epic1.getId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Нанять грузчиков", "Заключить договор", epic1.getId());
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Отпуск", "Подготовка к поездке");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Купить билеты", "Авиабилеты туда-обратно", epic2.getId());
        taskManager.addSubtask(subtask3);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());

        printAllTasks(taskManager);

        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("После изменения статусов");
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubtasks());

        taskManager.removeTask(task2.getId());
        taskManager.removeEpic(epic2.getId());

        System.out.println("После удаления задачи и эпика");
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubtasks());

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
