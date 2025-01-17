package catering.businesslogic.kitchen;


public interface TaskEventReceiver {
    void updateSummarySheetCreated(SummarySheet sumSheet);
    void updateAddedTask(Task task);
    void updateModifyTask(Task task);
    void updateRegCompletedTask(Task task);
    void updateDeleteAssignment(Task task);
    void updateAssignTask(Task task);
    void updateSummarySheetOpened(SummarySheet summarySheet); // Aggiunto per coerenza
}