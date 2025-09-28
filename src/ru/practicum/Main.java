package ru.practicum;

import ru.practicum.manager.Managers;
import ru.practicum.manager.TaskManager;
import ru.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = taskManager.addTask(new Task("Купить продукты", "Сделать покупки в магазине"));
        Task task2 = taskManager.addTask(new Task("Вымыть машину", "Помыть салон"));

        Epic epic1 = taskManager.addEpic(new Epic("Переезд", "Организовать переезд в новую квартиру"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("Упаковать вещи", "Собрать коробки", epic1.getId()));
        Subtask subtask1 = taskManager.getSubtaskById(subtaskId1);
        int subtaskId2 = taskManager.addSubtask(new Subtask("Нанять грузчиков", "Заключить договор", epic1.getId()));
        Subtask subtask2 = taskManager.getSubtaskById(subtaskId2);

        Epic epic2 = taskManager.addEpic(new Epic("Отпуск", "Подготовка к поездке"));
        int subtaskId3 = taskManager.addSubtask(new Subtask("Купить билеты", "Авиабилеты туда-обратно", epic2.getId()));
        Subtask subtask3 = taskManager.getSubtaskById(subtaskId3);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getTaskById(task1.getId());

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

        System.out.println("История после удаления:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
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
