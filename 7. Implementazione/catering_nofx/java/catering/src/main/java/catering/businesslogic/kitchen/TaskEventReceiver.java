package catering.businesslogic.kitchen;


import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

public interface TaskEventReceiver {
    void updateSummarySheetCreated(SummarySheet summary);
    void updateSummarySheetOpened(SummarySheet summary);
    void updateSummarySheetDeleted(SummarySheet summary);
    void updateAddedTask(Task task);
    void updateModifyTask(Task task);
    void updateRegCompletedTask(Task task);
    void updateDeleteAssignment(Task task);
    void updateAssignTask(Task task, Turn turn, Cook cook, String quantity, int time, int portions);
    void updatePreparationsOptimized(SummarySheet summary, Task task, Cook cook, Turn turn);
    void updatePreparationsVerified(SummarySheet summary, Task task, Cook cook, Turn turn);
}
