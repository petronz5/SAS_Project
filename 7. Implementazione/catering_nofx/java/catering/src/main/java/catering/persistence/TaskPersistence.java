package catering.persistence;

import catering.businesslogic.kitchen.SummarySheet;
import catering.businesslogic.kitchen.Task;
import catering.businesslogic.kitchen.TaskEventReceiver;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

public class TaskPersistence implements TaskEventReceiver {

    @Override
    public void updateSummarySheetCreated(SummarySheet summarySheet) {
        SummarySheet.saveNewSummarySheet(summarySheet);
        System.out.println("TaskPersistence: SummarySheet created and saved (ID=" + summarySheet.getId() + ").");
    }

    @Override
    public void updateSummarySheetDeleted(SummarySheet sumSheet) {
        System.out.println("TaskPersistence: SummarySheet deleted (ID=" + sumSheet.getId() + ").");
    }

    @Override
    public void updateSummarySheetOpened(SummarySheet summarySheet) {
        System.out.println("TaskPersistence: SummarySheet opened (ID=" + summarySheet.getId() + ").");
    }

    @Override
    public void updateAddedTask(Task task) {
        // Salva la nuova Task nel DB
        Task.saveNewTask(task);
        System.out.println("TaskPersistence: Task added and saved (ID=" + task.getId() + ").");
    }

    @Override
    public void updateModifyTask(Task task) {
        // Salva le modifiche apportate alla Task nel DB
        Task.saveModifiedTask(task);
        System.out.println("TaskPersistence: Task modified and updated (ID=" + task.getId() + ").");
    }

    @Override
    public void updateRegCompletedTask(Task task) {
        Task.saveModifiedTask(task);
        System.out.println("TaskPersistence: Task marked as completed (ID=" + task.getId() + ").");
    }

    @Override
    public void updateDeleteAssignment(Task task) {
        Task.deleteTaskAssignment(task);
        System.out.println("TaskPersistence: Task deleted (ID=" + task.getId() + ").");
    }

    @Override
    public void updateAssignTask(Task task, Turn turn, Cook cook, String quantity, int time, int portions) {
        Task.saveAssignedTask(task, turn, cook, quantity, time, portions);
        System.out.println("TaskPersistence: Task assigned (Task ID=" + task.getId() +
                ", Turn ID=" + turn.getId() + ", Cook ID=" + cook.getId() + ").");
    }

    @Override
    public void updatePreparationsOptimized(SummarySheet sumSheet, Task task, Cook cook, Turn turn) {
        Task.saveModifiedTask(task);
        System.out.println("TaskPersistence: Preparations optimized for Task ID=" + task.getId() +
                " in SummarySheet ID=" + sumSheet.getId() + ".");
    }

    @Override
    public void updatePreparationsVerified(SummarySheet sumSheet, Task task, Cook cook, Turn turn) {
        Task.saveModifiedTask(task);
        System.out.println("TaskPersistence: Preparations verified for Task ID=" + task.getId() +
                " in SummarySheet ID=" + sumSheet.getId() + ".");
    }
}
