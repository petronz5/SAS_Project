package catering.businesslogic.kitchen;


import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

public interface TaskEventReceiver {
    void updateSummarySheetCreated(SummarySheet sumSheet);
    void updateSummarySheetOpened(SummarySheet summarySheet);
    void updateSummarySheetDeleted(SummarySheet sumSheet);
    void updateAddedTask(Task task);
    void updateModifyTask(Task task);
    void updateRegCompletedTask(Task task);
    void updateDeleteAssignment(Task task);
    void updateAssignTask(Task task, Turn turn, Cook cook, String quantity, int time, int portions);
    void updatePreparationsOptimized(SummarySheet sumSheet, Task task, Cook cook, Turn turn);
    void updatePreparationsVerified(SummarySheet sumSheet, Task task, Cook cook, Turn turn);
}
