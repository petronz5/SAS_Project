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
    }

    @Override
    public void updateSummarySheetOpened(SummarySheet summarySheet) {
        // Nessuna azione necessaria per l'apertura del foglio riepilogativo
    }

    @Override
    public void updateAddedTask(Task task) {
        Task.saveNewTask(task);
    }

    @Override
    public void updateModifyTask(Task task) {
        Task.saveModifiedTask(task);
    }

    @Override
    public void updateRegCompletedTask(Task task) {

    }

    @Override
    public void updateDeleteAssignment(Task task) {
        Task.deleteTaskAssignment(task);
    }

    @Override
    public void updateAssignTask(Task task, Turn turn, Cook cook, String quantity, int time, int portions) {
        Task.saveAssignedTask(task, turn, cook, quantity, time, portions);
    }
}
