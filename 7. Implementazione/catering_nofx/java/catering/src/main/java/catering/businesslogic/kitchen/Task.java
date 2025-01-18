package catering.businesslogic.kitchen;

import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.*;
import java.util.ArrayList;

public class Task {
    private int id;
    private int summarySheetId;
    private int portions;
    private String quantity;
    private int estimatedTime;
    private boolean completed;

    private Recipe recipe;

    public Task(Recipe recipe) {
        this.recipe = recipe;
        this.portions = 0;
        this.quantity = "";
        this.estimatedTime = 0;
        this.completed = false;
    }

    // -------------------------------------
    // GETTERS & SETTERS
    // -------------------------------------
    public int getSummarySheetId() {
        return summarySheetId;
    }

    public void setSummarySheetId(int summarySheetId) {
        this.summarySheetId = summarySheetId;
    }

    public int getId() {
        return id;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // -------------------------------------
    // PERSISTENCE METHODS
    // -------------------------------------

    /**
     * Salva una nuova Task su DB, usando executeUpdate() e recuperando la chiave generata
     */
    public static void saveNewTask(Task task) {
        String insertSQL = "INSERT INTO catering.Tasks " +
                "(summary_sheet_id, recipe_id, portions, quantity, estimated_time, completed) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Eseguiamo un singolo INSERT
        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:8889/catering?serverTimezone=UTC",
                        "root",
                        "root"
                );
                PreparedStatement ps = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            // Log di debug
            System.out.println("Eseguo INSERT singolo (NON batch) con i seguenti valori:");
            System.out.println("summary_sheet_id: " + task.getSummarySheetId());
            System.out.println("recipe_id: " + task.getRecipe().getId());
            System.out.println("portions: " + task.getPortions());
            System.out.println("quantity: " + task.getQuantity());
            System.out.println("estimated_time: " + task.getEstimatedTime());
            System.out.println("completed: " + task.isCompleted());

            // Bind dei parametri
            ps.setInt(1, task.getSummarySheetId());
            ps.setInt(2, task.getRecipe().getId());
            ps.setInt(3, task.getPortions());
            ps.setString(4, task.getQuantity());
            ps.setInt(5, task.getEstimatedTime());
            ps.setBoolean(6, task.isCompleted());

            // Eseguiamo l'INSERT
            ps.executeUpdate();

            // Recuperiamo l'ID generato
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.id = rs.getInt(1);
                    System.out.println("Generated Task ID: " + task.id);
                } else {
                    System.out.println("Generated ID not found in getGeneratedKeys().");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Se non è stato assegnato alcun ID, solleviamo eccezione
        if (task.id == 0) {
            throw new IllegalStateException("Task ID is not generated properly");
        }
    }


    /**
     * Salva la modifica di una Task esistente.
     */
    public static void saveModifiedTask(Task task) {
        if (task.id <= 0) {
            throw new IllegalArgumentException("Task ID is invalid or not set");
        }

        String updateSQL = "UPDATE catering.Tasks " +
                "SET portions = ?, quantity = ?, estimated_time = ?, completed = ? " +
                "WHERE id = ?";

        PersistenceManager.executeUpdate(updateSQL, ps -> {
            ps.setInt(1, task.portions);
            ps.setString(2, task.quantity);
            ps.setInt(3, task.estimatedTime);
            ps.setBoolean(4, task.completed);
            ps.setInt(5, task.id);
        });
    }

    /**
     * Elimina la Task dal DB (se esiste).
     */
    public static void deleteTaskAssignment(Task task) {
        String deleteSQL = "DELETE FROM catering.Tasks WHERE id = ?";
        PersistenceManager.executeUpdate(deleteSQL, ps -> ps.setInt(1, task.id));
    }

    /**
     * Salva un'assegnazione della Task a un Turn e un Cook
     * (in base alla logica che hai implementato altrove).
     */
    public static void saveAssignedTask(Task task, Turn turn, Cook cook, String quantity, int estimatedTime, int portions) {
        String insertSQL = "INSERT INTO catering.TaskAssignments " +
                "(task_id, turn_id, cook_id, quantity, estimated_time, portions) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:8889/catering?serverTimezone=UTC",
                        "root",
                        "root"
                );
                PreparedStatement ps = conn.prepareStatement(insertSQL)
        ) {
            System.out.println("Inserisco in TaskAssignments:");
            System.out.println("task_id=" + task.getId() +
                    ", turn_id=" + turn.getId() +
                    ", cook_id=" + cook.getId() +
                    ", quantity=" + quantity +
                    ", estimatedTime=" + estimatedTime +
                    ", portions=" + portions);

            ps.setInt(1, task.getId());
            ps.setInt(2, turn.getId());
            ps.setInt(3, cook.getId());
            ps.setString(4, quantity);
            ps.setInt(5, estimatedTime);
            ps.setInt(6, portions);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica tutte le Task di un determinato SummarySheet.
     */
    public static ArrayList<Task> loadTasksForSheet(int sheetId) {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM catering.Tasks WHERE summary_sheet_id = " + sheetId;

        PersistenceManager.executeQuery(query, rs -> {
            // Carichiamo i campi
            Task task = new Task(null);  // Recipe non caricata qui, se vuoi, la puoi caricare con loadRecipeById
            task.id = rs.getInt("id");
            task.portions = rs.getInt("portions");
            task.quantity = rs.getString("quantity");
            task.estimatedTime = rs.getInt("estimated_time");
            task.completed = rs.getBoolean("completed");
            // Se serve recipe:
            // int recipeId = rs.getInt("recipe_id");
            // Recipe rec = Recipe.loadRecipeById(recipeId);
            // task.setRecipe(rec);

            tasks.add(task);
        });

        return tasks;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", summarySheetId=" + summarySheetId +
                ", portions=" + portions +
                ", quantity='" + quantity + '\'' +
                ", estimatedTime=" + estimatedTime +
                ", completed=" + completed +
                ", recipe=" + (recipe != null ? recipe.getName() : "null") +
                '}';
    }
}
