public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Купить продукты", "Сделать покупки в магазине", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Вымыть машину", "Помыть салон", Status.NEW);
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Переезд", "Организовать переезд в новую квартиру");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Упаковать вещи", "Собрать коробки", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Нанять грузчиков", "Заключить договор", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask2);

        Epic epic2 = new Epic("Отпуск", "Подготовка к поездке");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Купить билеты", "Авиабилеты туда-обратно", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask3);

        System.out.println("Список всех задач");
        System.out.println(taskManager.getAllTasks());

        System.out.println("Список всех эпиков");
        System.out.println(taskManager.getAllEpics());

        System.out.println("Список всех подзадач");
        System.out.println(taskManager.getAllSubtasks());

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
}
