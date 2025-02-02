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
import java.util.Map;

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

    public SummarySheet openSummarySheetById(int sheetId) throws UseCaseLogicException {
        SummarySheet summarySheet = SummarySheet.loadSummarySheetById(sheetId);
        if (summarySheet == null) {
            throw new UseCaseLogicException();
        }
        this.currentSummarySheet = summarySheet;
        this.notifySummarySheetOpened(summarySheet);
        return summarySheet;
    }

    public void deleteSummarySheet(SummarySheet summary) throws UseCaseLogicException {
        if (summary == null || !summary.getTasks().isEmpty()) {
            throw new UseCaseLogicException();
        }
        SummarySheet.deleteSummarySheet(summary.getId());
        if (currentSummarySheet == summary) {
            currentSummarySheet = null;
        }
        this.notifySummarySheetDeleted(summary);
    }

    public Task addTask(Recipe recipe) throws UseCaseLogicException {
        if (currentSummarySheet == null) {
            System.err.println("Errore: currentSummarySheet è NULL nel TaskManager!");
            throw new UseCaseLogicException();
        }
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        System.out.println("Aggiunta nuova Task alla scheda ID=" + currentSummarySheet.getId());

        // Creazione della Task
        Task task = currentSummarySheet.addTask(recipe);

        int diners = CatERing.getInstance().getEventManager().getCurrentEvent().getDiners();
        if (diners > 0) {
            task.setPortions(diners);
        }

        this.notifyTaskAdded(task);

        return task;
    }

    public void sortTasks(Map<String, Object> criteria) throws UseCaseLogicException {
        if (currentSummarySheet == null) {
            throw new UseCaseLogicException();
        }

        currentSummarySheet.sortPreparations(criteria);
        System.out.println("Sorting completed based on criteria: " + criteria);
    }

    public Task modifyTask(Task task, String quantity, Integer time, Integer portions) throws UseCaseLogicException {
        // Controlla se l'utente corrente è uno chef
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }

        // ALT: Controllo sui turni (turn != null && turn.perfDate > LocalDate.now())
        if (task.getInvolvedTurns() != null) {
            for (Turn turn : task.getInvolvedTurns()) {
                if (turn.getExpirationDate() != null && turn.getExpirationDate().toLocalDate().isBefore(LocalDate.now())) {
                    throw new UseCaseLogicException();
                }
            }
        }

        // ALT: Controllo sui cuochi assegnati (cook != null && turn.containsCook(cook))
        if (task.getInvolvedCooks() != null) {
            for (Cook cook : task.getInvolvedCooks()) {
                boolean cookInTurn = false;
                for (Turn turn : task.getInvolvedTurns()) {
                    if (turn.containsCook(cook)) {
                        cookInTurn = true;
                        break;
                    }
                }
                if (!cookInTurn) {
                    throw new UseCaseLogicException();
                }
            }
        }

        Task modifiedTask = currentSummarySheet.modifyTask(task, portions, quantity, time);

        this.notifyTaskModified(modifiedTask);
        return modifiedTask;
    }


    public void deleteAssignment(Task task, Cook cook, Turn turn) throws UseCaseLogicException {
        // Controlla se l'utente corrente è uno chef
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }
        currentSummarySheet.deleteAssignment(task, cook, turn);

        this.notifyTaskDeleted(task);
    }


    public Task assignTask(Task task, Turn turn, Cook cook, String quantity, Integer portions, Integer time) throws UseCaseLogicException {
        System.out.println("Verifica Task nel SummarySheet...");

        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            System.out.println("Errore: La Task non è presente nel SummarySheet.");
            throw new UseCaseLogicException();
        }

        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        if (turn != null && turn.getExpirationDate() != null && turn.getExpirationDate().toLocalDate().isBefore(LocalDate.now())) {
            throw new UseCaseLogicException();
        }

        if (cook != null && !turn.containsCook(cook)) {
            throw new UseCaseLogicException();
        }

        Task assignedTask = currentSummarySheet.assignTask(task, turn, cook, quantity, portions, time);

        // Aggiunta dei turni e dei cuochi come mostrato nel DSD
        if (turn != null) {
            assignedTask.addTurn(turn);
        }
        if (cook != null) {
            assignedTask.addCook(cook);
        }
        this.notifyTaskAssigned(assignedTask, turn, cook, quantity, portions, time);

        return assignedTask;
    }

    public ArrayList<Recipe> viewRecipeBook() {
        // Otteniamo l'elenco delle ricette
        return CatERing.getInstance().getRecipeManager().getRecipes();
    }

    public Task regCompletedTask(Task task) throws UseCaseLogicException {
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        // Verifico che il foglio riepilogativo corrente e il task siano validi
        if (currentSummarySheet == null || !currentSummarySheet.getTasks().contains(task)) {
            throw new UseCaseLogicException();
        }

        Task completedTask = currentSummarySheet.regCompletedTask(task);

        this.notifyTaskCompleted(completedTask);

        return completedTask;
    }

    public void optimizePreparations(Task task, Cook cook, Turn turn) throws UseCaseLogicException {
        if (!CatERing.getInstance().getUserManager().getCurrentUser().isChef()) {
            throw new UseCaseLogicException();
        }

        if (currentSummarySheet == null) {
            throw new UseCaseLogicException();
        }

        if (task != null && !currentSummarySheet.getTasks().contains(task)) {
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
    private void notifySummarySheetCreated(SummarySheet summary) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetCreated(summary);
        }
    }

    private void notifySummarySheetOpened(SummarySheet summary) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetOpened(summary);
        }
    }

    private void notifySummarySheetDeleted(SummarySheet summary) {
        for (TaskEventReceiver receiver : eventReceivers) {
            receiver.updateSummarySheetDeleted(summary);
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
