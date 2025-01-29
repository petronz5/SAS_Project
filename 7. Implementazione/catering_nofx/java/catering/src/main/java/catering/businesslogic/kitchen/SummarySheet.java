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

    public int getId() {
        return id;
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

    public Task modifyTask(Task task, Integer portions, String quantity, Integer time) {
        task.setQuantity(quantity);
        if (time != null) task.setEstimatedTime(time);
        if (portions != null) task.setPortions(portions);

        // Gestione dei turni e dei cuochi
        if (task.getInvolvedTurns() != null) {
            task.getInvolvedTurns().clear();
        }
        if (task.getInvolvedCooks() != null) {
            task.getInvolvedCooks().clear();
        }

        return task;
    }

    public static void deleteSummarySheet(int sheetId) {
        String deleteQuery = "DELETE FROM catering.SummarySheets WHERE id = ?";
        PersistenceManager.executeUpdate(deleteQuery, ps -> ps.setInt(1, sheetId));
    }

    public void deleteAssignment(Task task, Cook cook, Turn turn) {
        this.myTasks.remove(task);
    }

    public void sortPreparations(Map<String, Object> parameters) {
        if (parameters.containsKey("sortBy") && parameters.get("sortBy").equals("estimatedTime")) {
            myTasks.sort((t1, t2) -> Integer.compare(t1.getEstimatedTime(), t2.getEstimatedTime()));
        }
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

    public void optimizePreparations(Task task, Cook cook, Turn turn) {
        if (task != null) {
            System.out.println("Optimizing specific task: " + task.getId());
            if (cook != null) {
                System.out.println("Assigning cook: " + cook.getId());
            }
            if (turn != null) {
                System.out.println("Assigning turn: " + turn.getId());
            }
        } else {
            myTasks.sort((t1, t2) -> Integer.compare(t1.getEstimatedTime(), t2.getEstimatedTime()));
            for (Task t : myTasks) {
                if (!t.isCompleted()) {
                    System.out.println("Consolidating task: " + t.getId());
                } else {
                    System.out.println("Task already completed: " + t.getId());
                }
            }
        }
        System.out.println("Preparations optimized.");
    }

    public void verifyPreparations(Task task, Cook cook, Turn turn) {
        if (task != null) {
            // Verifica specifica
            if (task.getEstimatedTime() <= 0 || task.getPortions() <= 0) {
                myTasks.remove(task); // Rimuove il task non valido
                System.out.println("Invalid task removed: " + task.getId());
            } else {
                if (cook != null) task.addCook(cook);
                if (turn != null) task.addTurn(turn);
                System.out.println("Task is valid: " + task.getId());
            }
        } else {
            // Verifica globale
            myTasks.removeIf(t -> t.getEstimatedTime() <= 0 || t.getPortions() <= 0);
            System.out.println("Invalid tasks removed.");
        }
        System.out.println("Preparations verified.");
    }

    public Task assignTask(Task task, Turn turn, Cook cook, String quantity, Integer portions, Integer time) {
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

    public static SummarySheet loadSummarySheet(ServiceInfo service, EventInfo event) {
        SummarySheet[] sheetHolder = {null}; // Usa un array per evitare problemi di finalità

        // Incorpora i parametri direttamente nella query
        String query = "SELECT * FROM catering.SummarySheets WHERE service_id = " + service.getId() + " AND event_id = " + event.getId();
        PersistenceManager.executeQuery(query, rs -> {
            if (rs.next()) {
                SummarySheet sheet = new SummarySheet();
                sheet.id = rs.getInt("id");
                sheet.referredService = service;
                sheet.referredEvent = event;
                sheetHolder[0] = sheet; // Assegna il valore all'array
            }
        });

        SummarySheet sheet = sheetHolder[0]; // Recupera l'oggetto dall'array

        if (sheet != null) {
            sheet.myTasks = Task.loadTasksForSheet(sheet.id);
        }
        return sheet;
    }

    public static SummarySheet loadSummarySheetById(int sheetId){
        SummarySheet sheet = new SummarySheet();
        String query = "SELECT * FROM catering.SummarySheets WHERE id = " + sheetId;

        PersistenceManager.executeQuery(query, rs -> {
            if (rs.next()) {
                sheet.id = rs.getInt("id");
                // Imposta altri campi di sheet se necessario
            }
        });

        // Carica i task associati
        sheet.myTasks = Task.loadTasksForSheet(sheet.id);
        return sheet;
    }

}
