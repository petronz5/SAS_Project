package catering.businesslogic.kitchen;

import catering.businesslogic.recipe.Recipe;
import catering.businesslogic.turns.Cook;
import catering.businesslogic.turns.Turn;
import catering.persistence.PersistenceManager;
import catering.persistence.ResultHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Task {
    private int id;
    private int summarySheetId;
    private int portions;
    private String quantity;
    private int estimatedTime;
    private boolean completed;
    private ArrayList<Turn> involvedTurns = new ArrayList<>();
    private ArrayList<Cook> involvedCooks = new ArrayList<>();

    private Recipe recipe;

    public Task(Recipe recipe) {
        this.recipe = recipe;
        this.portions = 0;
        this.quantity = "";
        this.estimatedTime = 0;
        this.completed = false;

        this.involvedCooks = new ArrayList<>();
        this.involvedTurns = new ArrayList<>();
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
        if (portions < 0) {
            throw new IllegalArgumentException("Le porzioni non possono essere negative.");
        }
        this.portions = portions;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public ArrayList<Cook> getInvolvedCooks() {
        return involvedCooks;
    }

    public ArrayList<Turn> getInvolvedTurns() {
        return involvedTurns;
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

    public Task regCompletedTask() {
        this.setCompleted(true);
        Task.saveModifiedTask(this);
        return this;
    }

    public void verifyAndUpdate(Cook cook, Turn turn) {
        if (cook != null) {
            this.addCook(cook);
        }
        if (turn != null) {
            this.addTurn(turn);
        }
    }

    public Task modifyTask(String quantity, Integer time, Integer portions) {
        if (quantity != null) {
            this.quantity = quantity;
        }
        if (time != null) {
            this.estimatedTime = time;
        }
        if (portions != null) {
            this.portions = portions;
        }

        this.involvedCooks.clear();
        this.involvedTurns.clear();

        return this;
    }

    public Task assignTask(Turn turn, Cook cook, String quantity, Integer portions, Integer time) {
        if (quantity != null) {
            this.quantity = quantity;
        }
        if (time != null) {
            this.estimatedTime = time;
        }
        if (portions != null) {
            this.portions = portions;
        }

        if (turn != null) {
            this.addTurn(turn);
        }
        if (cook != null) {
            this.addCook(cook);
        }

        return this;
    }


    public void addCook(Cook cook) {
        if (cook != null && !this.involvedCooks.contains(cook)) {
            this.involvedCooks.add(cook);
        }
    }

    public void addTurn(Turn turn) {
        if (turn != null && !this.involvedTurns.contains(turn)) {
            this.involvedTurns.add(turn);
        }
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

        try (
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:8889/catering?serverTimezone=UTC",
                        "root",
                        "root"
                );
                PreparedStatement ps = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            // Log di debug
            System.out.println("Eseguo INSERT singolo con i seguenti valori:");
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
            int rowsInserted = ps.executeUpdate();

            // Debug: Verifica se l'INSERT è avvenuto
            if (rowsInserted == 0) {
                throw new SQLException("Task non inserita, nessuna riga aggiunta!");
            }

            // Recuperiamo l'ID generato
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.id = rs.getInt(1);
                    System.out.println("Generated Task ID: " + task.id);
                } else {
                    System.out.println("Generated ID not found in getGeneratedKeys().");
                    throw new SQLException("Errore nel recupero dell'ID della Task.");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Se non è stato assegnato alcun ID, solleviamo eccezione
        if (task.id <= 0) {
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
            int recipeId = rs.getInt("recipe_id");
            Recipe rec = Recipe.loadRecipeById(recipeId);
            if (rec == null) {
                System.err.println("Recipe con ID=" + recipeId + " non trovata per Task ID=" + rs.getInt("id"));
                return;
            }

            Task task = new Task(rec);
            task.id = rs.getInt("id");
            task.summarySheetId = rs.getInt("summary_sheet_id");
            task.portions = rs.getInt("portions");
            task.quantity = rs.getString("quantity");
            task.estimatedTime = rs.getInt("estimated_time");
            task.completed = rs.getBoolean("completed");

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
