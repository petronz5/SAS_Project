package catering.businesslogic.kitchen;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;

import java.time.LocalDate;
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

    public SummarySheet openSummarySheet(ServiceInfo service, EventInfo event) throws UseCaseLogicException {
        SummarySheet summarySheet = SummarySheet.loadSummarySheet(service, event);
        if (summarySheet == null) {
            throw new UseCaseLogicException();
        }
        this.currentSummarySheet = summarySheet;
        this.notifySummarySheetOpened(summarySheet);
        return summarySheet;
    }


    public void deleteSummarySheet(SummarySheet summarySheet) throws UseCaseLogicException {
        if (summarySheet == null || !summarySheet.getTasks().isEmpty()) {
            throw new UseCaseLogicException();
        }
        SummarySheet.deleteSummarySheet(summarySheet.getId());
        if (currentSummarySheet == summarySheet) {
            currentSummarySheet = null;
        }
        this.notifySummarySheetDeleted(summarySheet);
    }


    public Task addTask(Recipe recipe) throws UseCaseLogicException {

        if(!CatERing.getInstance().getUserManager().getCurrentUser().isChef()){
            throw new UseCaseLogicException();
        }

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

        // Verifica che il compito non sia completato
        if (task.isCompleted()) {
            throw new UseCaseLogicException();
        }

        Task modifiedTask = currentSummarySheet.modifyTask(task, portions, quantity, time);
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

    public Task assignTask(Task task, Turn turn, Cook cook, String quantity, Integer portions, Integer time) throws UseCaseLogicException {
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }
        if (turn.getExpirationDate() != null && turn.getExpirationDate().toLocalDate().isBefore(LocalDate.now())) {
            throw new UseCaseLogicException();
        }

        if (!turn.containsCook(cook)) {
            throw new UseCaseLogicException();
        }

        Task assignedTask = currentSummarySheet.assignTask(task, turn, cook, quantity, portions, time);
        this.notifyTaskAssigned(assignedTask, turn, cook, quantity, portions , time);
        return assignedTask;
    }

    public ArrayList<Recipe> viewRecipeBook() {
        // Otteniamo l'elenco delle ricette
        return CatERing.getInstance().getRecipeManager().getRecipes();
    }

    public Task regCompletedTask(Task task) throws UseCaseLogicException {
        // Controlla se l'utente corrente è uno chef
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        // Verifica che il foglio riepilogativo corrente e il task siano validi
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }

        // Imposta il task come completato
        task.setCompleted(true);

        // Salva il cambiamento nel database
        Task.saveModifiedTask(task);

        // Notifica agli observer il completamento del task
        this.notifyTaskCompleted(task);

        return task;
    }


    public void optimizePreparations(Task task, Cook cook, Turn turn) throws UseCaseLogicException {
        if (currentSummarySheet == null) {
            throw new UseCaseLogicException();
        }
        currentSummarySheet.optimizePreparations(task, cook, turn);
        this.notifyPreparationsOptimized(task, cook, turn);
    }


    public void verifyPreparations(Task task, Cook cook, Turn turn) throws UseCaseLogicException {
        if (currentSummarySheet == null) {
            throw new UseCaseLogicException();
        }
        currentSummarySheet.verifyPreparations(task, cook, turn);
        this.notifyPreparationsVerified(task, cook, turn);
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

    private void notifySummarySheetDeleted(SummarySheet summarySheet) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetDeleted(summarySheet);
        }
    }

    private void notifyTaskAdded(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateAddedTask(task);
        }
    }

    private void notifyTaskCompleted(Task task) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateRegCompletedTask(task);
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

    private void notifyTaskAssigned(Task task, Turn turn, Cook cook, String quantity, int time, int portions) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateAssignTask(task, turn, cook, quantity, time, portions);
        }
    }

    private void notifyPreparationsOptimized(Task task, Cook cook, Turn turn) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updatePreparationsOptimized(currentSummarySheet, task, cook, turn);
        }
    }
    private void notifyPreparationsVerified(Task task, Cook cook, Turn turn) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updatePreparationsVerified(currentSummarySheet, task, cook, turn);
        }
    }



}
