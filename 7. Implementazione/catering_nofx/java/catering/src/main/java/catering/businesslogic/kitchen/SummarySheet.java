package catering.businesslogic.kitchen;

import catering.businesslogic.CatERing;
import catering.businesslogic.UseCaseLogicException;
import catering.businesslogic.event.EventInfo;
import catering.businesslogic.event.ServiceInfo;
import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;
import catering.persistence.BatchUpdateHandler;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SummarySheet {
    private int id;
    private ServiceInfo referredService;
    private EventInfo referredEvent;
    private ArrayList<Task> myTasks;

    public SummarySheet() {
        this.myTasks = new ArrayList<>();
    }

    public void setReferredService(ServiceInfo service) {
        this.referredService = service;
    }

    public void setReferredEvent(EventInfo event) {
        this.referredEvent = event;
    }

    public ArrayList<Task> getTasks() {
        return myTasks;
    }

    public Task addTask(Recipe recipe) {
        Task task = new Task(recipe);
        task.setSummarySheetId(this.id);
        this.myTasks.add(task);
        return task;
    }

    public Task modifyTask(Task task, String quantity, Integer time, Integer portions) {
        task.setQuantity(quantity);
        if (time != null) task.setEstimatedTime(time);
        if (portions != null) task.setPortions(portions);
        return task;
    }

    public void deleteAssignment(Task task, Cook cook, Turn turn) {
        this.myTasks.remove(task);
    }

    public void sortPreparations(Map<String, Object> parameters) {
        // Esempio di criterio di ordinamento basato sul tempo stimato
        if (parameters.containsKey("sortBy") && parameters.get("sortBy").equals("estimatedTime")) {
            myTasks.sort((t1, t2) -> Integer.compare(t1.getEstimatedTime(), t2.getEstimatedTime()));
        }
        // Puoi aggiungere altri criteri di ordinamento in base ai parametri
        System.out.println("Preparations sorted based on criteria: " + parameters);
    }

    public void markPreparationAsCooked(Task task) {
        if (myTasks.contains(task)) {
            task.setCompleted(true); // Imposta la Task come completata
            Task.saveModifiedTask(task); // Salva le modifiche al DB
            System.out.println("Task marked as cooked: " + task.getId());
        } else {
            System.out.println("Task not found in the current SummarySheet.");
        }
    }

    public void optimizePreparations() {
        // Ottimizzazione base: ordina le preparazioni per tempo stimato (ascendente)
        myTasks.sort((t1, t2) -> Integer.compare(t1.getEstimatedTime(), t2.getEstimatedTime()));

        // Puoi aggiungere logiche di ottimizzazione più avanzate (es. raggruppamento per cuochi, turni, ecc.)
        System.out.println("Preparations optimized.");
    }

    public void verifyPreparations() {
        boolean allValid = true;
        for (Task task : myTasks) {
            if (task.getEstimatedTime() <= 0 || task.getPortions() <= 0) {
                System.out.println("Invalid task found: " + task.getId());
                allValid = false;
            }
        }
        if (allValid) {
            System.out.println("All preparations are valid.");
        } else {
            System.out.println("Some preparations failed verification.");
        }
    }





    public Task assignTask(Task task, Turn turn, Cook cook, String quantity, Integer time, Integer portions) {
        // Assign task logic (shift, cook)
        task.setQuantity(quantity);
        task.setEstimatedTime(time);
        task.setPortions(portions);
        return task;
    }

    // STATIC METHODS FOR PERSISTENCE
    public static void saveNewSummarySheet(SummarySheet sheet) {
        String insert = "INSERT INTO catering.SummarySheets (service_id, event_id) VALUES (?, ?)";
        PersistenceManager.executeBatchUpdate(insert, 1, new BatchUpdateHandler() {
            @Override
            public void handleBatchItem(PreparedStatement ps, int batchCount) throws SQLException {
                ps.setInt(1, sheet.referredService.getId());
                ps.setInt(2, sheet.referredEvent.getId());
            }

            @Override
            public void handleGeneratedIds(ResultSet rs, int count) throws SQLException {
                if (rs.next()) {
                    sheet.id = rs.getInt(1);
                }
            }
        });
    }

    public static SummarySheet loadSummarySheet(int eventId) {
        SummarySheet sheet = new SummarySheet();
        String query = "SELECT * FROM catering.SummarySheets WHERE event_id = " + eventId;
        PersistenceManager.executeQuery(query, rs -> {
            sheet.id = rs.getInt("id");
            // Load other fields if necessary
        });

        // Load tasks
        sheet.myTasks = Task.loadTasksForSheet(sheet.id);
        return sheet;
    }


    public static SummarySheet loadSummarySheetById(int sheetId){
        SummarySheet sheet = new SummarySheet();
        String query = "SELECT * FROM catering.SummarySheets WHERE id = " + sheetId;
        PersistenceManager.executeQuery(query, rs -> {
            sheet.id = rs.getInt("id");
        });

        sheet.myTasks = Task.loadTasksForSheet(sheet.id);
        return sheet;
    }



}
