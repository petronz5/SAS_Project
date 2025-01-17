package catering.businesslogic.kitchen;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

import java.util.ArrayList;

public class TaskManager {
    private SummarySheet currentSummarySheet;
    private ArrayList<TaskEventReceiver> eventReceivers;

    public TaskManager() {
        this.eventReceivers = new ArrayList<>();
    }

    public void setCurrentSummarySheet(SummarySheet summarySheet) {
        this.currentSummarySheet = summarySheet;
    }

    public SummarySheet generateSummarySheet(ServiceInfo service, EventInfo event) {
        SummarySheet summarySheet = new SummarySheet();
        summarySheet.setReferredService(service);
        summarySheet.setReferredEvent(event);
        this.currentSummarySheet = summarySheet;
        this.notifySummarySheetCreated(summarySheet);
        return summarySheet;
    }

    public SummarySheet openSummarySheet(int eventId) {
        SummarySheet summarySheet = SummarySheet.loadSummarySheet(eventId);
        this.currentSummarySheet = summarySheet;
        this.notifySummarySheetOpened(summarySheet);
        return summarySheet;
    }

    public Task addTask(Recipe recipe) throws UseCaseLogicException {
        if (currentSummarySheet == null) {
            throw new UseCaseLogicException();
        }
        Task task = currentSummarySheet.addTask(recipe);
        this.notifyTaskAdded(task);
        return task;
    }

    public Task modifyTask(Task task, String quantity, Integer time, Integer portions) throws UseCaseLogicException {
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }
        Task modifiedTask = currentSummarySheet.modifyTask(task, quantity, time, portions);
        this.notifyTaskModified(modifiedTask);
        return modifiedTask;
    }

    public void deleteAssignment(Task task, Cook cook, Turn turn) throws UseCaseLogicException {
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }
        currentSummarySheet.deleteAssignment(task, cook, turn);
        this.notifyTaskDeleted(task);
    }

    public Task assignTask(Task task, Turn turn, Cook cook, String quantity, Integer time, Integer portions) throws UseCaseLogicException {
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }
        Task assignedTask = currentSummarySheet.assignTask(task, turn, cook, quantity, time, portions);
        this.notifyTaskAssigned(assignedTask);
        return assignedTask;
    }

    public void addEventReceiver(TaskEventReceiver receiver) {
        this.eventReceivers.add(receiver);
    }

    public void removeEventReceiver(TaskEventReceiver receiver) {
        this.eventReceivers.remove(receiver);
    }

    // Notifiche agli observer
    private void notifySummarySheetCreated(SummarySheet summarySheet) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetCreated(summarySheet);
        }
    }

    private void notifySummarySheetOpened(SummarySheet summarySheet) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetOpened(summarySheet);
        }
    }

    private void notifyTaskAdded(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateAddedTask(task);
        }
    }

    private void notifyTaskModified(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateModifyTask(task);
        }
    }

    private void notifyTaskDeleted(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateDeleteAssignment(task);
        }
    }

    private void notifyTaskAssigned(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateAssignTask(task);
        }
    }
}