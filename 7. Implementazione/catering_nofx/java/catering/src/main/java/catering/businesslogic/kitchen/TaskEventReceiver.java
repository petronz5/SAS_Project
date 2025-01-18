package catering.businesslogic.kitchen;


import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

public interface TaskEventReceiver {
    void updateSummarySheetCreated(SummarySheet sumSheet);
    void updateAddedTask(Task task);
    void updateModifyTask(Task task);
    void updateRegCompletedTask(Task task);
    void updateDeleteAssignment(Task task);
    void updateAssignTask(Task task, Turn turn, Cook cook, String quantity, int time, int portions);
    void updateSummarySheetOpened(SummarySheet summarySheet); // Aggiunto per coerenza
}